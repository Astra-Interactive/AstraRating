package ru.astrainteractive.astrarating.command.reload

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.command.api.CommandParser
import ru.astrainteractive.astralibs.permission.PermissionManager
import ru.astrainteractive.astrarating.plugin.RatingPermission

class ReloadCommandParser(
    override val alias: String,
    private val permissionManager: PermissionManager
) : CommandParser<ReloadCommandParser.Result> {
    sealed interface Result {
        data object NoPermission : Result
        class Success(val sender: CommandSender) : Result
    }

    override fun parse(args: Array<out String>, sender: CommandSender): Result {
        (sender as? Player)?.let {
            val hasPermission = permissionManager.hasPermission(it.uniqueId, RatingPermission.Reload)
            if (!hasPermission) return Result.NoPermission
        }
        return Result.Success(sender)
    }
}
