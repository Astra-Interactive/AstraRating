package ru.astrainteractive.astrarating.command.reload

import org.bukkit.command.CommandSender

internal interface ReloadCommand {
    class Result(val sender: CommandSender)
}
