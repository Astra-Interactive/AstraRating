package com.astrainteractive.astrarating.commands

import CommandManager
import com.astrainteractive.astrarating.utils.AstraPermission
import ru.astrainteractive.astralibs.commands.DSLCommand

/**
 * Reload command handler
 */

/**
 * This function called only when atempreload being called
 *
 * Here you should also check for permission
 */
fun CommandManager.reload() = DSLCommand.invoke("aratingreload") {
    if (!AstraPermission.Reload.hasPermission(this.sender)) return@invoke
    CommandManager.reload(this.sender)
}






