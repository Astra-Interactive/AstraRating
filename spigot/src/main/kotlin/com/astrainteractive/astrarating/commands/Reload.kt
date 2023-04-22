package com.astrainteractive.astrarating.commands

import CommandManager
import com.astrainteractive.astrarating.plugin.AstraPermission
import ru.astrainteractive.astralibs.AstraLibs
import ru.astrainteractive.astralibs.commands.registerCommand

/**
 * Reload command handler
 */

/**
 * This function called only when atempreload being called
 *
 * Here you should also check for permission
 */
fun CommandManager.reload() = AstraLibs.instance.registerCommand("aratingreload") {
    if (!AstraPermission.Reload.hasPermission(this.sender)) return@registerCommand
    CommandManager.reload(this.sender)
}






