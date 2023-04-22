package com.astrainteractive.astrarating.commands

import com.astrainteractive.astrarating.domain.api.RatingDBApi
import com.astrainteractive.astrarating.dto.RatingType
import com.astrainteractive.astrarating.domain.use_cases.InsertUserUseCase
import com.astrainteractive.astrarating.dto.UserDTO
import com.astrainteractive.astrarating.exception.ValidationException
import com.astrainteractive.astrarating.exception.ValidationExceptionHandler
import com.astrainteractive.astrarating.gui.ratings.RatingsGUI
import com.astrainteractive.astrarating.models.UserModel
import com.astrainteractive.astrarating.modules.ServiceLocator
import com.astrainteractive.astrarating.plugin.AstraPermission
import com.astrainteractive.astrarating.plugin.EmpireConfig
import com.astrainteractive.astrarating.plugin.PluginTranslation
import com.astrainteractive.astrarating.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.async.BukkitAsync
import ru.astrainteractive.astralibs.async.BukkitMain
import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astralibs.utils.uuid
import kotlin.jvm.Throws

object RatingCommandController {

    private val translation: PluginTranslation by ServiceLocator.translation
    private val config: EmpireConfig by ServiceLocator.config
    private val databaseApi: RatingDBApi by ServiceLocator.dbApi
    private val insertUserUseCase: InsertUserUseCase by ServiceLocator.insertUserUseCase

    @Throws(ValidationException::class)
    suspend fun addRating(
        ratingCreator: CommandSender,
        message: String,
        ratedPlayer: OfflinePlayer?,
        rating: Int,
        typeDTO: RatingType
    ): Result<*> = kotlin.runCatching {
        if (!AstraPermission.Vote.hasPermission(ratingCreator)) throw ValidationException.NoPermission(ratingCreator)

        if (ratingCreator !is Player) throw ValidationException.OnlyPlayerCommand(ratingCreator)

        if (ratedPlayer == null || ratedPlayer.firstPlayed == 0L)
            throw ValidationException.PlayerNotExists(ratingCreator)

        if (ratedPlayer == ratingCreator) throw ValidationException.SamePlayer(ratingCreator)

        if (System.currentTimeMillis() - ratingCreator.firstPlayed < config.minTimeOnServer)
            throw ValidationException.NotEnoughOnServer(ratingCreator)

        val maxVotesPerDay = AstraPermission.MaxRatePerDay.permissionSize(ratingCreator) ?: config.maxRatingPerDay
        val maxVotePerPlayer =
            AstraPermission.SinglePlayerPerDay.permissionSize(ratingCreator) ?: config.maxRatingPerPlayer



        if (config.needDiscordLinked) {
            val discordMember = getLinkedDiscordID(ratingCreator)
                ?.let { getDiscordMember(it) }
                ?: throw ValidationException.DiscordNotLinked(ratingCreator)

            val wasInDiscordSince = discordMember.timeJoined.toInstant().toEpochMilli()
            if (System.currentTimeMillis() - wasInDiscordSince < config.minTimeOnDiscord)
                throw ValidationException.NotEnoughOnDiscord(ratingCreator)
        }

        val todayVotedAmount = databaseApi.countPlayerTotalDayRated(ratingCreator.name).getOrNull() ?: 0
        val votedOnPlayerAmount =
            databaseApi.countPlayerOnPlayerDayRated(ratingCreator.name, ratedPlayer.name ?: "NULL").getOrNull() ?: 0


        if (todayVotedAmount > maxVotesPerDay)
            throw ValidationException.AlreadyMaxDayVotes(ratingCreator)

        if (votedOnPlayerAmount > maxVotePerPlayer)
            throw ValidationException.AlreadyMaxVotesOnPlayer(ratingCreator)


        if (message.length < config.minMessageLength || message.length > config.maxMessageLength)
            throw ValidationException.WrongMessageLength(ratingCreator)

        val playerCreatedID = insertUserUseCase(UserModel(ratingCreator.uniqueId, ratingCreator.name))
        val playerReportedID =
            insertUserUseCase(UserModel(ratedPlayer.uniqueId, ratedPlayer?.name ?: "NULL"))
        if (playerCreatedID == null || playerReportedID == null) throw ValidationException.DBException(ratingCreator)
        databaseApi.insertUserRating(
            reporter = UserDTO(
                id = playerCreatedID,
                minecraftUUID = ratingCreator.uuid,
                minecraftName = ratingCreator.name ?: "-"
            ),
            reported = UserDTO(
                id = playerReportedID,
                minecraftUUID = ratedPlayer.uuid,
                minecraftName = ratedPlayer.name ?: "-"
            ),
            message = message,
            type = typeDTO,
            ratingValue = rating
        )

        if (rating > 0)
            ratingCreator.sendMessage(translation.likedUser.replace("%player%", ratedPlayer.name ?: "-"))
        else
            ratingCreator.sendMessage(translation.dislikedUser.replace("%player%", ratedPlayer.name ?: "-"))
    }

    fun rating(sender: CommandSender) {
        PluginScope.launch(Dispatchers.BukkitAsync) {
            val inventory = RatingsGUI(sender as Player)
            withContext(Dispatchers.BukkitMain) {
                inventory.open()
            }
        }
    }
}