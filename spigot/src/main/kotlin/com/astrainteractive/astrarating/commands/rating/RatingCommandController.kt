package com.astrainteractive.astrarating.commands.rating

import com.astrainteractive.astrarating.commands.rating.di.RatingCommandControllerModule
import com.astrainteractive.astrarating.dto.RatingType
import com.astrainteractive.astrarating.dto.UserDTO
import com.astrainteractive.astrarating.exception.ValidationException
import com.astrainteractive.astrarating.models.UserModel
import com.astrainteractive.astrarating.plugin.AstraPermission
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.getValue
import ru.astrainteractive.astralibs.utils.uuid

class RatingCommandController(
    private val module: RatingCommandControllerModule
) {
    private val dispatchers by module.dispatchers
    private val scope by module.scope
    private val translation by module.translation
    private val config by module.config
    private val databaseApi by module.dbApi
    private val insertUserUseCase by module.insertUseCase

    @Throws(ValidationException::class)
    @Suppress("CyclomaticComplexMethod")
    suspend fun addRating(
        ratingCreator: CommandSender,
        message: String,
        ratedPlayer: OfflinePlayer?,
        rating: Int,
        typeDTO: RatingType
    ): Result<*> = kotlin.runCatching {
        if (!AstraPermission.Vote.hasPermission(ratingCreator)) throw ValidationException.NoPermission(ratingCreator)

        if (ratingCreator !is Player) throw ValidationException.OnlyPlayerCommand(ratingCreator)

        if (ratedPlayer == null || ratedPlayer.firstPlayed == 0L) {
            throw ValidationException.PlayerNotExists(ratingCreator)
        }

        if (ratedPlayer == ratingCreator) throw ValidationException.SamePlayer(ratingCreator)

        if (System.currentTimeMillis() - ratingCreator.firstPlayed < config.minTimeOnServer) {
            throw ValidationException.NotEnoughOnServer(ratingCreator)
        }

        val maxVotesPerDay = AstraPermission.MaxRatePerDay.permissionSize(ratingCreator) ?: config.maxRatingPerDay
        val maxVotePerPlayer =
            AstraPermission.SinglePlayerPerDay.permissionSize(ratingCreator) ?: config.maxRatingPerPlayer

//        if (config.needDiscordLinked) {
//            val discordMember = getLinkedDiscordID(ratingCreator)
//                ?.let { getDiscordMember(it) }
//                ?: throw ValidationException.DiscordNotLinked(ratingCreator)
//
//            val wasInDiscordSince = discordMember.timeJoined.toInstant().toEpochMilli()
//            if (System.currentTimeMillis() - wasInDiscordSince < config.minTimeOnDiscord)
//                throw ValidationException.NotEnoughOnDiscord(ratingCreator)
//        }

        val todayVotedAmount = databaseApi.countPlayerTotalDayRated(ratingCreator.name).getOrNull() ?: 0
        val votedOnPlayerAmount =
            databaseApi.countPlayerOnPlayerDayRated(ratingCreator.name, ratedPlayer.name ?: "NULL").getOrNull() ?: 0

        if (todayVotedAmount > maxVotesPerDay) {
            throw ValidationException.AlreadyMaxDayVotes(ratingCreator)
        }

        if (votedOnPlayerAmount > maxVotePerPlayer) {
            throw ValidationException.AlreadyMaxVotesOnPlayer(ratingCreator)
        }

        if (message.length < config.minMessageLength || message.length > config.maxMessageLength) {
            throw ValidationException.WrongMessageLength(ratingCreator)
        }

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

        if (rating > 0) {
            ratingCreator.sendMessage(translation.likedUser.replace("%player%", ratedPlayer.name ?: "-"))
        } else {
            ratingCreator.sendMessage(translation.dislikedUser.replace("%player%", ratedPlayer.name ?: "-"))
        }
    }

    fun rating(sender: CommandSender) {
        scope.launch(dispatchers.BukkitAsync) {
            val inventory = module.ratingsGUIFactory(sender as Player).build()
            withContext(dispatchers.BukkitMain) {
                inventory.open()
            }
        }
    }
}
