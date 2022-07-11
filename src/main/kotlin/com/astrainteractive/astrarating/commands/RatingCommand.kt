package com.astrainteractive.astrarating.commands

import CommandManager
import com.astrainteractive.astralibs.AstraLibs
import com.astrainteractive.astralibs.async.AsyncHelper
import com.astrainteractive.astralibs.registerCommand
import com.astrainteractive.astrarating.api.DatabaseApi
import com.astrainteractive.astrarating.api.use_cases.InsertUserUseCase
import com.astrainteractive.astrarating.sqldatabase.entities.User
import com.astrainteractive.astrarating.sqldatabase.entities.UserRating
import com.astrainteractive.astrarating.utils.uuid
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.time.OffsetDateTime

/**
 * /arating reload
 * /arating like/dislike <player> <message>
 * /arating rating <player>
 */
fun CommandManager.ratingCommand() = AstraLibs.registerCommand("arating") { sender, args ->
    val argument = args.getOrNull(0)
    if (argument == null) {
        sender.sendMessage("Possible arguments: [reload; like; dislike; rating]")
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
    val player = args.getOrNull(1)?.let { Bukkit.getOfflinePlayer(it) }
    val message = args.getOrNull(2) ?: ""
    if (sender !is Player) {
        sender.sendMessage("Команда доступна только игрокам")
        return
    }
    if (player == null) {
        sender.sendMessage("Такого игрока нет")
        return
    }
    if (player == sender) {
        sender.sendMessage("Вы не можете поставить рейтинг самому себе")
        return
    }
    if (message.length < 10) {
        sender.sendMessage("Причина рейтинга должна быть длиннее")
        return
    }
    val holder = LikeDislikeHolder(player, message, sender)
    AsyncHelper.launch {
        val insertUserUseCase = InsertUserUseCase()
        val playerCreatedID = insertUserUseCase(holder.sender as OfflinePlayer)
        val playerReportedID = insertUserUseCase(holder.player)
        if (playerCreatedID == null || playerReportedID == null) {
            sender.sendMessage("Произошла ошибка в базе данных")
            return@launch
        }
        callback(holder, playerCreatedID, playerReportedID)
    }
}

fun RatingCommandController.like(sender: CommandSender, args: Array<out String>) {
    likeOrDislike(sender, args) { holder, createdID, reportedID ->
        val ratingEntity = UserRating(-1, createdID, reportedID, 1, holder.message)
        DatabaseApi.insertUserRating(ratingEntity)
    }
}

fun RatingCommandController.dislike(sender: CommandSender, args: Array<out String>) {
    likeOrDislike(sender, args) { holder, createdID, reportedID ->
        val ratingEntity = UserRating(-1, createdID, reportedID, -1, holder.message)
        DatabaseApi.insertUserRating(ratingEntity)
    }
}

fun RatingCommandController.rating(sender: CommandSender, args: Array<out String>) {

}