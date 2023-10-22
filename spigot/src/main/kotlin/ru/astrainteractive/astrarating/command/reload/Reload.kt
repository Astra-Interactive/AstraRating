package ru.astrainteractive.astrarating.command.reload

import CommandManager
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.command.registerCommand
import ru.astrainteractive.astrarating.plugin.RatingPermission

/**
 * Reload command handler
 */

/**
 * This function called only when atempreload being called
 *
 * Here you should also check for permission
 */
fun CommandManager.reload() = plugin.registerCommand("aratingreload") {
    (sender as? Player)?.let { player ->
        val canReload = permissionManager.hasPermission(player.uniqueId, RatingPermission.Reload)
        if (canReload) return@let
        translation.noPermission
            .let(translationContext::toComponent)
            .run(sender::sendMessage)
        return@registerCommand
    }

    translation.reload
        .let(translationContext::toComponent)
        .run(sender::sendMessage)

    plugin.reloadPlugin()

    translation.reloadComplete
        .let(translationContext::toComponent)
        .run(sender::sendMessage)
}
