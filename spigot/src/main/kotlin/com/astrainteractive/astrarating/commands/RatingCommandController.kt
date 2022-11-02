package com.astrainteractive.astrarating.commands

import com.astrainteractive.astrarating.domain.SQLDatabase
import com.astrainteractive.astrarating.domain.api.IRatingAPI
import com.astrainteractive.astrarating.domain.use_cases.InsertUserUseCase
import com.astrainteractive.astrarating.domain.entities.tables.dto.UserRatingDTO
import com.astrainteractive.astrarating.exception.ValidationException
import com.astrainteractive.astrarating.exception.ValidationExceptionHandler
import com.astrainteractive.astrarating.gui.ratings.RatingsGUI
import com.astrainteractive.astrarating.modules.ConfigProvider
import com.astrainteractive.astrarating.modules.DatabaseApiModule
import com.astrainteractive.astrarating.modules.InsertUserUseCaseModule
import com.astrainteractive.astrarating.modules.TranslationProvider
import com.astrainteractive.astrarating.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.async.PluginScope

object RatingCommandController {

    private val translation: PluginTranslation
        get() = TranslationProvider.value
    private val config: EmpireConfig
        get() = ConfigProvider.value
    private val databaseApi: IRatingAPI
        get() = DatabaseApiModule.value
    private val insertUserUseCase: InsertUserUseCase
        get() = InsertUserUseCaseModule.value

    fun addRating(
        ratingCreator: CommandSender,
        args: Array<out String>,
        rating: Int,
    ) = ValidationExceptionHandler.intercept {
        if (!AstraPermission.Vote.hasPermission(ratingCreator)) throw ValidationException.NoPermission(ratingCreator)

        if (ratingCreator !is Player) throw ValidationException.OnlyPlayerCommand(ratingCreator)

        val ratedPlayer = args.getOrNull(1)?.let { Bukkit.getOfflinePlayer(it) }

        if (ratedPlayer == null || ratedPlayer.firstPlayed == 0L)
            throw ValidationException.PlayerNotExists(ratingCreator)

        if (ratedPlayer == ratingCreator) throw ValidationException.SamePlayer(ratingCreator)

        if (System.currentTimeMillis() - ratingCreator.firstPlayed < config.minTimeOnServer)
            throw ValidationException.NotEnoughOnServer(ratingCreator)

        val maxVotesPerDay = AstraPermission.MaxRatePerDay.permissionSize(ratingCreator) ?: config.maxRatingPerDay
        val maxVotePerPlayer =
            AstraPermission.SinglePlayerPerDay.permissionSize(ratingCreator) ?: config.maxRatingPerPlayer


        PluginScope.launch(Dispatchers.IO) {

            ValidationExceptionHandler.suspendIntercept(this) {
                if (config.needDiscordLinked) {
                    val discordMember = getLinkedDiscordID(ratingCreator)?.let { getDiscordMember(it) }
                        ?: throw ValidationException.DiscordNotLinked(ratingCreator)

                    val wasInDiscordSince = discordMember.timeJoined.toInstant().toEpochMilli()
                    if (System.currentTimeMillis() - wasInDiscordSince < config.minTimeOnDiscord) throw ValidationException.NotEnoughOnDiscord(
                        ratingCreator
                    )
                }
            }

            val todayVotedAmount = databaseApi.countPlayerTotalDayRated(ratingCreator.name) ?: 0
            val votedOnPlayerAmount = databaseApi.countPlayerOnPlayerDayRated(ratingCreator.name, ratedPlayer.name?:"NULL") ?: 0




            if (todayVotedAmount > maxVotesPerDay) {
                ratingCreator.sendMessage(translation.alreadyMaxDayVotes)
                return@launch
            }

            if (votedOnPlayerAmount > maxVotePerPlayer) {
                ratingCreator.sendMessage(translation.alreadyMaxPlayerVotes)
                return@launch
            }

            val message = args.toList().subList(2, args.size).joinToString(" ")
            if (message.length < config.minMessageLength || message.length > config.maxMessageLength) {
                ratingCreator.sendMessage(translation.wrongMessageLen)
                return@launch
            }

            val playerCreatedID = insertUserUseCase(InsertUserUseCase.Param(ratingCreator.uniqueId,ratingCreator.name))
            val playerReportedID = insertUserUseCase(InsertUserUseCase.Param(ratedPlayer.uniqueId,ratedPlayer?.name?:"NULL"))
            if (playerCreatedID == null || playerReportedID == null) {
                ratingCreator.sendMessage(translation.dbError)
                return@launch
            }

            val ratingEntity = UserRatingDTO(SQLDatabase.NON_EXISTS_KEY, playerCreatedID, playerReportedID, rating, message)
            databaseApi.insertUserRating(ratingEntity)
            if (rating > 0)
                ratingCreator.sendMessage(translation.likedUser.replace("%player%", args[1]))
            else
                ratingCreator.sendMessage(translation.dislikedUser.replace("%player%", args[1]))
        }
    }

    fun rating(sender: CommandSender, args: Array<out String>) {
        PluginScope.launch {
            RatingsGUI(sender as Player).open()
        }
    }
}