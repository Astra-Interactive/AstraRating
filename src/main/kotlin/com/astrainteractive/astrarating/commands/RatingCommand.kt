package com.astrainteractive.astrarating.commands

import CommandManager
import com.astrainteractive.astrarating.domain.SQLDatabase.Companion.NON_EXISTS_KEY
import ru.astrainteractive.astralibs.AstraLibs
import ru.astrainteractive.astralibs.utils.registerCommand
import com.astrainteractive.astrarating.domain.api.DatabaseApi
import com.astrainteractive.astrarating.domain.api.use_cases.InsertUserUseCase
import com.astrainteractive.astrarating.domain.entities.UserRating
import com.astrainteractive.astrarating.exception.ValidationException
import com.astrainteractive.astrarating.exception.ValidationExceptionHandler
import com.astrainteractive.astrarating.gui.ratings.RatingsGUI
import com.astrainteractive.astrarating.modules.ConfigProvider
import com.astrainteractive.astrarating.modules.DatabaseApiModule
import com.astrainteractive.astrarating.modules.InsertUserUseCaseModule
import com.astrainteractive.astrarating.modules.TranslationProvider
import com.astrainteractive.astrarating.utils.*
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.async.PluginScope

private val translation: PluginTranslation
    get() = TranslationProvider.value
private val config: EmpireConfig
    get() = ConfigProvider.value

/**
 * /arating reload
 * /arating like/dislike <player> <message>
 * /arating rating <player>
 */
fun CommandManager.ratingCommand() = AstraLibs.registerCommand("arating") { sender, args ->
    val argument = args.getOrNull(0)
    if (argument == null) {
        sender.sendMessage(translation.wrongUsage)
        return@registerCommand
    }
    when (argument) {
        "like" -> RatingCommandController.addRating(sender, args, 1)
        "dislike" -> RatingCommandController.addRating(sender, args, -1)
        "rating" -> RatingCommandController.rating(sender, args)
        "reload" -> CommandManager.reload(sender)
    }
}

private val databaseApi: DatabaseApi
    get() = DatabaseApiModule.value
private val insertUserUseCase: InsertUserUseCase
    get() = InsertUserUseCaseModule.value

object RatingCommandController

data class LikeDislikeHolder(
    val player: OfflinePlayer,
    val message: String,
    val sender: CommandSender,
)


fun RatingCommandController.addRating(
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

    PluginScope.launch {
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
        val todayVotedAmount = databaseApi.countPlayerTotalDayRated(ratingCreator as Player) ?: 0
        val votedOnPlayerAmount = databaseApi.countPlayerOnPlayerDayRated(ratingCreator, ratedPlayer) ?: 0



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
        val playerCreatedID = insertUserUseCase(ratingCreator)
        val playerReportedID = insertUserUseCase(ratedPlayer)
        if (playerCreatedID == null || playerReportedID == null) {
            ratingCreator.sendMessage(translation.dbError)
            return@launch
        }
        val ratingEntity = UserRating(NON_EXISTS_KEY, playerCreatedID, playerReportedID, rating, message)
        databaseApi.insertUserRating(ratingEntity)
        if (rating > 0)
            ratingCreator.sendMessage(translation.likedUser.replace("%player%", args[1]))
        else
            ratingCreator.sendMessage(translation.dislikedUser.replace("%player%", args[1]))
    }
}

fun RatingCommandController.rating(sender: CommandSender, args: Array<out String>) {
    PluginScope.launch {
        RatingsGUI(sender as Player).open()
    }
}