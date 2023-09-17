package com.astrainteractive.astrarating.commands.rating

import CommandManager
import com.astrainteractive.astrarating.dto.RatingType
import com.astrainteractive.astrarating.exception.ValidationExceptionHandler
import com.astrainteractive.astrarating.modules.impl.RootModuleImpl
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import ru.astrainteractive.astralibs.command.registerCommand
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.getValue

/**
 * /arating reload
 * /arating like/dislike <player> <message>
 * /arating rating <player>
 */
fun CommandManager.ratingCommand() = plugin.registerCommand("arating") {
    val validationExceptionHandler by Provider {
        ValidationExceptionHandler(translation)
    }
    val argument = args.getOrNull(0)
    if (argument == null) {
        sender.sendMessage(RootModuleImpl.translation.value.wrongUsage)
        return@registerCommand
    }
    when (argument) {
        "like", "dislike" -> {
            val ratedPlayer = args.getOrNull(1)?.let { Bukkit.getOfflinePlayer(it) }
            val message = args.toList().subList(2, args.size).joinToString(" ")
            val amount = if (argument == "like") 1 else -1

            scope.launch(dispatchers.BukkitAsync) {
                ratingCommandController.addRating(
                    ratingCreator = sender,
                    rating = amount,
                    message = message,
                    ratedPlayer = ratedPlayer,
                    typeDTO = RatingType.USER_RATING
                ).onFailure(validationExceptionHandler::handle)
            }
        }

        "rating" -> ratingCommandController.rating(sender)
        "reload" -> Bukkit.dispatchCommand(sender, "aratingreload")
    }
}
