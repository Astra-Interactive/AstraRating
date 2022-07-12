package com.astrainteractive.astrarating.commands


import CommandManager
import com.astrainteractive.astralibs.AstraLibs
import com.astrainteractive.astralibs.registerTabCompleter
import com.astrainteractive.astralibs.withEntry
import org.bukkit.Bukkit

/**
 * Tab completer for your plugin which is called when player typing commands
 */
fun CommandManager.tabCompleter() = AstraLibs.registerTabCompleter("arating") { sender, args ->
    if (args.size == 1)
        return@registerTabCompleter listOf("reload", "like", "dislike", "rating")
    if (args.size == 2 && (args[0].equals("like", ignoreCase = true) || args[1].equals("dislike", ignoreCase = true)))
        return@registerTabCompleter Bukkit.getOnlinePlayers().mapNotNull { it.name }
    return@registerTabCompleter listOf<String>()
}



