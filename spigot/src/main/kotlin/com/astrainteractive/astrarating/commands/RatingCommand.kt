package com.astrainteractive.astrarating.commands

import CommandManager
import com.astrainteractive.astrarating.domain.SQLDatabase.Companion.NON_EXISTS_KEY
import ru.astrainteractive.astralibs.AstraLibs
import ru.astrainteractive.astralibs.utils.registerCommand
import com.astrainteractive.astrarating.domain.api.IRatingAPI
import com.astrainteractive.astrarating.domain.use_cases.InsertUserUseCase
import com.astrainteractive.astrarating.domain.entities.UserRating
import com.astrainteractive.astrarating.exception.ValidationException
import com.astrainteractive.astrarating.exception.ValidationExceptionHandler
import com.astrainteractive.astrarating.gui.ratings.RatingsGUI
import com.astrainteractive.astrarating.modules.ConfigProvider
import com.astrainteractive.astrarating.modules.DatabaseApiModule
import com.astrainteractive.astrarating.modules.InsertUserUseCaseModule
import com.astrainteractive.astrarating.modules.TranslationProvider
import com.astrainteractive.astrarating.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.async.PluginScope

/**
 * /arating reload
 * /arating like/dislike <player> <message>
 * /arating rating <player>
 */
fun CommandManager.ratingCommand() = AstraLibs.registerCommand("arating") { sender, args ->
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