package com.astrainteractive.astrarating.commands

import CommandManager
import com.astrainteractive.astralibs.AstraLibs
import com.astrainteractive.astralibs.async.AsyncHelper
import com.astrainteractive.astralibs.registerCommand
import com.astrainteractive.astrarating.api.DatabaseApi
import com.astrainteractive.astrarating.api.use_cases.InsertUserUseCase
import com.astrainteractive.astrarating.gui.ratings.RatingsGUI
import com.astrainteractive.astrarating.sqldatabase.entities.UserRating
import com.astrainteractive.astrarating.utils.*
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * /arating reload
 * /arating like/dislike <player> <message>
 * /arating rating <player>
 */
fun CommandManager.ratingCommand() = AstraLibs.registerCommand("arating") { sender, args ->
    val argument = args.getOrNull(0)
    if (argument == null) {
        sender.sendMessage(Translation.wrongUsage)
        return@registerCommand
    }
    when (argument) {
        "like" -> RatingCommandController.addRating(sender, args, 1)
        "dislike" -> RatingCommandController.addRating(sender, args, -1)
        "rating" -> RatingCommandController.rating(sender, args)
        "reload" -> CommandManager.reload(sender)
    }
}

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
) {

    if (!AstraPermission.Vote.hasPermission(ratingCreator)) {
        ratingCreator.sendMessage(Translation.noPermission)
        return
    }
    val ratedPlayer = args.getOrNull(1)?.let { Bukkit.getOfflinePlayer(it) }
    if (ratingCreator !is Player) {
        ratingCreator.sendMessage(Translation.onlyPlayerCommand)
        return
    }
    if (ratedPlayer == null || ratedPlayer.firstPlayed==0L) {
        ratingCreator.sendMessage(Translation.playerNotExists)
        return
    }
    if (ratedPlayer == ratingCreator) {
        ratingCreator.sendMessage(Translation.cantRateSelf)
        return
    }
    if (System.currentTimeMillis() - ratingCreator.firstPlayed < Config.minTimeOnServer) {
        ratingCreator.sendMessage(Translation.notEnoughOnServer)
        return
    }
    val message = args.toList().subList(2, args.size).joinToString(" ")

    AsyncHelper.launch {
        val discordMember = getLinkedDiscordID(ratingCreator)?.let { getDiscordMember(it) }
        if (Config.needDiscordLinked && discordMember == null) {
            ratingCreator.sendMessage(Translation.needDiscordLinked)
            return@launch
        }
        val discordTime = discordMember?.let {
            System.currentTimeMillis() - it.timeJoined.toInstant().toEpochMilli() > Config.minTimeOnDiscord
        } ?: !Config.needDiscordLinked
        if (!discordTime) {
            ratingCreator.sendMessage(Translation.notEnoughOnDiscord)
            return@launch
        }

        val todayVoted = DatabaseApi.countPlayerTotalDayRated(ratingCreator as Player) ?: 0
        val votedOnPlayer = DatabaseApi.countPlayerOnPlayerDayRated(ratingCreator, ratedPlayer) ?: 0

        val maxVotesPerDay = AstraPermission.MaxRatePerDay.permissionSize(ratingCreator) ?: Config.maxRatingPerDay
        val maxVotePerPlayer = AstraPermission.SinglePlayerPerDay.permissionSize(ratingCreator) ?: Config.maxRatingPerPlayer

        if (todayVoted > maxVotesPerDay) {
            ratingCreator.sendMessage(Translation.alreadyMaxDayVotes)
            return@launch
        }
        if (votedOnPlayer > maxVotePerPlayer) {
            ratingCreator.sendMessage(Translation.alreadyMaxPlayerVotes)
            return@launch
        }
        if (!IntRange(5, 30).contains(message.length)) {
            ratingCreator.sendMessage(Translation.wrongMessageLen)
            return@launch
        }
        val playerCreatedID = InsertUserUseCase(ratingCreator)
        val playerReportedID = InsertUserUseCase(ratedPlayer)
        if (playerCreatedID == null || playerReportedID == null) {
            ratingCreator.sendMessage(Translation.dbError)
            return@launch
        }

        val ratingEntity = UserRating(-1, playerCreatedID, playerReportedID, rating, message)
        DatabaseApi.insertUserRating(ratingEntity)
        if (rating > 0)
            ratingCreator.sendMessage(Translation.likedUser.replace("%player%", args[1]))
        else
            ratingCreator.sendMessage(Translation.dislikedUser.replace("%player%", args[1]))
    }
}

fun RatingCommandController.rating(sender: CommandSender, args: Array<out String>) {
    AsyncHelper.launch {
        RatingsGUI(sender as Player).open()
    }
}