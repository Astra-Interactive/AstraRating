package ru.astrainteractive.astrarating.command

import CommandManager
import org.bukkit.Bukkit

/**
 * Tab completer for your plugin which is called when player typing commands
 */
fun CommandManager.tabCompleter() = plugin.getCommand("arating")?.setTabCompleter { sender, command, label, args ->
    when {
        args.size == 1 -> listOf("reload", "like", "dislike", "rating")
        args.size == 2 && (
            args[0].equals("like", ignoreCase = true) || args[0].equals(
                "dislike",
                ignoreCase = true
            )
            ) -> return@setTabCompleter Bukkit.getOnlinePlayers().mapNotNull { it.name }

        args.size == 4 -> listOf("REASON")
        else -> Bukkit.getOnlinePlayers().mapNotNull { it.name }
    }
}
