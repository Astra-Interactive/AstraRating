package com.astrainteractive.astrarating.commands

import CommandManager
import com.astrainteractive.astrarating.dto.RatingType
import com.astrainteractive.astrarating.exception.ValidationExceptionHandler
import com.astrainteractive.astrarating.modules.ServiceLocator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.astrainteractive.astralibs.AstraLibs
import org.bukkit.Bukkit
import ru.astrainteractive.astralibs.async.BukkitAsync
import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.commands.registerCommand

/**
 * /arating reload
 * /arating like/dislike <player> <message>
 * /arating rating <player>
 */
fun CommandManager.ratingCommand() = AstraLibs.instance.registerCommand("arating") {
    val argument = args.getOrNull(0)
    if (argument == null) {
        sender.sendMessage(ServiceLocator.translation.value.wrongUsage)
        return@registerCommand
    }
    when (argument) {
        "like", "dislike" -> {
            val ratedPlayer = args.getOrNull(1)?.let { Bukkit.getOfflinePlayer(it) }
            val message = args.toList().subList(2, args.size).joinToString(" ")
            val amount = if (argument == "like") 1 else -1

            PluginScope.launch(Dispatchers.BukkitAsync) {
                RatingCommandController.addRating(
                    ratingCreator = sender,
                    rating = amount,
                    message = message,
                    ratedPlayer = ratedPlayer,
                    typeDTO = RatingType.USER_RATING
                ).onFailure(ValidationExceptionHandler::handle)
            }
        }

        "rating" -> RatingCommandController.rating(sender)
        "reload" -> CommandManager.reload(sender)
    }
}