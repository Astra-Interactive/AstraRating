package com.astrainteractive.astrarating.commands


import CommandManager
import com.astrainteractive.astralibs.AstraLibs
import com.astrainteractive.astralibs.registerTabCompleter
import com.astrainteractive.astralibs.withEntry

/**
 * Tab completer for your plugin which is called when player typing commands
 */
fun CommandManager.tabCompleter() = AstraLibs.registerTabCompleter("atemp") { sender, args ->
    if (args.isEmpty())
        return@registerTabCompleter listOf("atemp", "atempreload")
    if (args.size == 1)
        return@registerTabCompleter listOf("atemp", "atempreload").withEntry(args.last())
    return@registerTabCompleter listOf<String>()
}



