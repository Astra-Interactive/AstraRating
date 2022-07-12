package com.astrainteractive.astrarating.commands

import CommandManager
import com.astrainteractive.astralibs.AstraLibs
import com.astrainteractive.astralibs.async.AsyncHelper
import com.astrainteractive.astralibs.registerCommand
import com.astrainteractive.astrarating.api.DatabaseApi
import com.astrainteractive.astrarating.api.use_cases.InsertUserUseCase
import com.astrainteractive.astrarating.gui.ratings.RatingsGUI
import com.astrainteractive.astrarating.sqldatabase.entities.UserRating
import com.astrainteractive.astrarating.utils.AstraPermission
import com.astrainteractive.astrarating.utils.Config
import com.astrainteractive.astrarating.utils.Translation
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
        "like" -> RatingCommandController.like(sender, args)
        "dislike" -> RatingCommandController.dislike(sender, args)
        "rating" -> RatingCommandController.rating(sender, args)
    }
}

object RatingCommandController

data class LikeDislikeHolder(
    val player: OfflinePlayer,
    val message: String,
    val sender: CommandSender,
)

fun likeOrDislike(
    sender: CommandSender,
    args: Array<out String>,
    callback: suspend (holder: LikeDislikeHolder, playerCreatedID: Long, playerReportedID: Long) -> Unit
) {

    if (!AstraPermission.Vote.hasPermission(sender)) {
        sender.sendMessage(Translation.noPermission)
        return
    }
    val player = args.getOrNull(1)?.let { Bukkit.getOfflinePlayer(it) }
    if (sender !is Player) {
        sender.sendMessage(Translation.onlyPlayerCommand)
        return
    }
    if (player == null) {
        sender.sendMessage(Translation.playerNotExists)
        return
    }
    if (player == sender) {
        sender.sendMessage(Translation.cantRateSelf)
        return
    }
    val message = args.toList().subList(2, args.size).joinToString(" ")

    AsyncHelper.launch {
        val todayVoted = DatabaseApi.countPlayerTotalDayRated(sender as Player) ?: 0
        val votedOnPlayer = DatabaseApi.countPlayerOnPlayerDayRated(sender, player) ?: 0

        val maxVotesPerDay = AstraPermission.MaxRatePerDay.permissionSize(sender) ?: Config.maxRatingPerDay
        val maxVotePerPlayer = AstraPermission.SinglePlayerPerDay.permissionSize(sender) ?: Config.maxRatingPerPlayer

        if (todayVoted > maxVotesPerDay) {
            sender.sendMessage(Translation.alreadyMaxDayVotes)
            return@launch
        }
        if (votedOnPlayer > maxVotePerPlayer) {
            sender.sendMessage(Translation.alreadyMaxPlayerVotes)
            return@launch
        }
        if (!IntRange(5, 30).contains(message.length)) {
            sender.sendMessage(Translation.wrongMessageLen)
            return@launch
        }
        val holder = LikeDislikeHolder(player, message, sender)
        val insertUserUseCase = InsertUserUseCase()
        val playerCreatedID = insertUserUseCase(holder.sender as OfflinePlayer)
        val playerReportedID = insertUserUseCase(holder.player)
        if (playerCreatedID == null || playerReportedID == null) {
            sender.sendMessage(Translation.dbError)
            return@launch
        }
        callback(holder, playerCreatedID, playerReportedID)
    }
}

fun RatingCommandController.like(sender: CommandSender, args: Array<out String>) {
    likeOrDislike(sender, args) { holder, createdID, reportedID ->
        val ratingEntity = UserRating(-1, createdID, reportedID, 1, holder.message)
        DatabaseApi.insertUserRating(ratingEntity)
        sender.sendMessage(Translation.likedUser.replace("%player%", args[1]))
    }
}

fun RatingCommandController.dislike(sender: CommandSender, args: Array<out String>) {
    likeOrDislike(sender, args) { holder, createdID, reportedID ->
        val ratingEntity = UserRating(-1, createdID, reportedID, -1, holder.message)
        DatabaseApi.insertUserRating(ratingEntity)
        sender.sendMessage(Translation.dislikedUser.replace("%player%", args[1]))
    }
}

fun RatingCommandController.rating(sender: CommandSender, args: Array<out String>) {
    AsyncHelper.launch {
        RatingsGUI(sender as Player).open()
    }

}