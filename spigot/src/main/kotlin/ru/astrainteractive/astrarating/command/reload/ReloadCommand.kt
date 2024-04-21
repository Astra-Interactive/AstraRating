package ru.astrainteractive.astrarating.command.reload

import org.bukkit.command.CommandSender
import ru.astrainteractive.astralibs.command.api.command.BukkitCommand

interface ReloadCommand : BukkitCommand {
    sealed interface Result {
        data object NoPermission : Result
        class Success(val sender: CommandSender) : Result
    }

    class Input(val sender: CommandSender)
}
