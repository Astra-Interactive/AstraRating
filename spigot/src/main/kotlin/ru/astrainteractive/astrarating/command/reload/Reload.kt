package ru.astrainteractive.astrarating.command.reload

import CommandManager
import ru.astrainteractive.astralibs.command.registerCommand
import ru.astrainteractive.astrarating.plugin.AstraPermission

/**
 * Reload command handler
 */

/**
 * This function called only when atempreload being called
 *
 * Here you should also check for permission
 */
fun CommandManager.reload() = plugin.registerCommand("aratingreload") {
    if (!AstraPermission.Reload.hasPermission(sender)) {
        sender.sendMessage(translation.noPermission)
        return@registerCommand
    }
    sender.sendMessage(translation.reload)
    plugin.reloadPlugin()
    sender.sendMessage(translation.reloadComplete)
}
