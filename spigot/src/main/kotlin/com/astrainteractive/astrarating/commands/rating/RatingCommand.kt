package com.astrainteractive.astrarating.commands.rating

import CommandManager
import com.astrainteractive.astrarating.commands.rating.di.RatingCommandModule
import com.astrainteractive.astrarating.dto.RatingType
import com.astrainteractive.astrarating.exception.ValidationExceptionHandler
import com.astrainteractive.astrarating.modules.impl.RootModuleImpl
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.astralibs.commands.registerCommand
import ru.astrainteractive.astralibs.getValue

/**
 * /arating reload
 * /arating like/dislike <player> <message>
 * /arating rating <player>
 */
fun CommandManager.ratingCommand(
    plugin: JavaPlugin,
    module: RatingCommandModule
) = plugin.registerCommand("arating") {
    val controller by module.ratingCommandController
    val scope by module.scope
    val dispatchers by module.dispatchers
    val validationExceptionHandler = ValidationExceptionHandler(module.translation)
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
                controller.addRating(
                    ratingCreator = sender,
                    rating = amount,
                    message = message,
                    ratedPlayer = ratedPlayer,
                    typeDTO = RatingType.USER_RATING
                ).onFailure(validationExceptionHandler::handle)
            }
        }

        "rating" -> controller.rating(sender)
        "reload" -> Bukkit.dispatchCommand(sender, "aratingreload")
    }
}
