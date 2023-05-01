package com.astrainteractive.astrarating.commands.reload

import CommandManager
import com.astrainteractive.astrarating.AstraRating
import com.astrainteractive.astrarating.commands.di.CommandsModule
import com.astrainteractive.astrarating.plugin.AstraPermission
import ru.astrainteractive.astralibs.commands.registerCommand
import ru.astrainteractive.astralibs.getValue

/**
 * Reload command handler
 */

/**
 * This function called only when atempreload being called
 *
 * Here you should also check for permission
 */
fun CommandManager.reload(
    plugin: AstraRating,
    module: CommandsModule
) = plugin.registerCommand("aratingreload") {
    val translation by module.translation

    if (!AstraPermission.Reload.hasPermission(sender)) {
        sender.sendMessage(translation.noPermission)
        return@registerCommand
    }
    sender.sendMessage(translation.reload)
    plugin.reloadPlugin()
    sender.sendMessage(translation.reloadComplete)
}
