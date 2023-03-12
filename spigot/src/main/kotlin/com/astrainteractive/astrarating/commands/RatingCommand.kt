package com.astrainteractive.astrarating.commands

import CommandManager
import com.astrainteractive.astrarating.domain.entities.tables.dto.RatingTypeDTO
import ru.astrainteractive.astralibs.AstraLibs
import com.astrainteractive.astrarating.modules.TranslationProvider
import org.bukkit.Bukkit
import ru.astrainteractive.astralibs.commands.registerCommand

/**
 * /arating reload
 * /arating like/dislike <player> <message>
 * /arating rating <player>
 */
fun CommandManager.ratingCommand() = AstraLibs.instance.registerCommand("arating") {
    val argument = args.getOrNull(0)
    if (argument == null) {
        sender.sendMessage(TranslationProvider.value.wrongUsage)
        return@registerCommand
    }
    when (argument) {
        "like", "dislike" -> {
            val ratedPlayer = args.getOrNull(1)?.let { Bukkit.getOfflinePlayer(it) }
            val message = args.toList().subList(2, args.size).joinToString(" ")
            val amount = if (argument == "like") 1 else -1

            RatingCommandController.addRating(
                ratingCreator = sender,
                rating = amount,
                message = message,
                ratedPlayer = ratedPlayer,
                typeDTO = RatingTypeDTO.USER_RATING
            )
        }

        "rating" -> RatingCommandController.rating(sender, args)
        "reload" -> CommandManager.reload(sender)
    }
}