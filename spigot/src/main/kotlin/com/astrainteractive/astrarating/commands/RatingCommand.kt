package com.astrainteractive.astrarating.commands

import CommandManager
import ru.astrainteractive.astralibs.AstraLibs
import com.astrainteractive.astrarating.modules.TranslationProvider
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
        "like" -> RatingCommandController.addRating(sender, args, 1)
        "dislike" -> RatingCommandController.addRating(sender, args, -1)
        "rating" -> RatingCommandController.rating(sender, args)
        "reload" -> CommandManager.reload(sender)
    }
}