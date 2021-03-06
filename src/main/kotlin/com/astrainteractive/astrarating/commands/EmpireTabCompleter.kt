package com.astrainteractive.astrarating.commands


import CommandManager
import com.astrainteractive.astralibs.AstraLibs
import com.astrainteractive.astralibs.registerTabCompleter
import org.bukkit.Bukkit

/**
 * Tab completer for your plugin which is called when player typing commands
 */
fun CommandManager.tabCompleter() = AstraLibs.registerTabCompleter("arating") { sender, args ->
    return@registerTabCompleter when {
        args.size == 1 -> listOf("reload", "like", "dislike", "rating")
        args.size == 2 && (args[0].equals("like", ignoreCase = true) || args[0].equals("dislike", ignoreCase = true)) -> return@registerTabCompleter Bukkit.getOnlinePlayers().mapNotNull { it.name }
        else ->  Bukkit.getOnlinePlayers().mapNotNull { it.name }
    }
}
