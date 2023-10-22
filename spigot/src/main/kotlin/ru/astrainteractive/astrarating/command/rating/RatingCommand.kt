package ru.astrainteractive.astrarating.command.rating

import CommandManager
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import ru.astrainteractive.astralibs.command.registerCommand
import ru.astrainteractive.astralibs.command.types.OnlinePlayerArgument
import ru.astrainteractive.astralibs.command.types.PrimitiveArgumentType
import ru.astrainteractive.astrarating.dto.RatingType
import ru.astrainteractive.klibs.kdi.getValue

/**
 * /arating reload
 * /arating like/dislike <player> <message>
 * /arating rating <player>
 */
fun CommandManager.ratingCommand() = plugin.registerCommand("arating") {
    val argument = argument(0, PrimitiveArgumentType.String)
        .onFailure {
            val message = translationContext.toComponent(translation.wrongUsage)
            sender.sendMessage(message)
            return@registerCommand
        }.resultOrNull() ?: return@registerCommand

    when (argument) {
        "like", "dislike" -> {
            val ratedPlayer = argument(1, OnlinePlayerArgument).resultOrNull()
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
