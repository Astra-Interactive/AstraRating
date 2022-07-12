package com.astrainteractive.astrarating.commands

import CommandManager
import com.astrainteractive.astralibs.commands.AstraDSLCommand
import com.astrainteractive.astrarating.AstraRating
import com.astrainteractive.astrarating.utils.AstraPermission
import com.astrainteractive.astrarating.utils.Translation

/**
 * Reload command handler
 */

/**
 * This function called only when atempreload being called
 *
 * Here you should also check for permission
 */
fun CommandManager.reload() = AstraDSLCommand.command("aratingreload") {
    this.noPermission(AstraPermission.Reload.value) {
        sender.sendMessage(Translation.noPermission)
    } ?: return@command
    sender.sendMessage(Translation.reload)
    AstraRating.instance.reloadPlugin()
    sender.sendMessage(Translation.reloadComplete)
}






