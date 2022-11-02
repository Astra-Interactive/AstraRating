package com.astrainteractive.astrarating.commands

import CommandManager
import ru.astrainteractive.astralibs.commands.AstraDSLCommand

/**
 * Reload command handler
 */

/**
 * This function called only when atempreload being called
 *
 * Here you should also check for permission
 */
fun CommandManager.reload() = AstraDSLCommand.command("aratingreload") {
    CommandManager.reload(this.sender)
}






