package com.astrainteractive.astrarating.commands

import CommandManager
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.astralibs.commands.registerTabCompleter

/**
 * Tab completer for your plugin which is called when player typing commands
 */
fun CommandManager.tabCompleter(plugin: JavaPlugin) = plugin.registerTabCompleter("arating") {
    return@registerTabCompleter when {
        args.size == 1 -> listOf("reload", "like", "dislike", "rating")
        args.size == 2 && (
            args[0].equals("like", ignoreCase = true) || args[0].equals(
                "dislike",
                ignoreCase = true
            )
            ) -> return@registerTabCompleter Bukkit.getOnlinePlayers().mapNotNull { it.name }

        args.size == 4 -> listOf("REASON")
        else -> Bukkit.getOnlinePlayers().mapNotNull { it.name }
    }
}
