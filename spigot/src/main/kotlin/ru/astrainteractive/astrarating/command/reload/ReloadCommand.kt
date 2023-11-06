package ru.astrainteractive.astrarating.command.reload

import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.astralibs.command.api.Command
import ru.astrainteractive.astralibs.permission.PermissionManager
import ru.astrainteractive.astralibs.string.BukkitTranslationContext
import ru.astrainteractive.astrarating.AstraRating
import ru.astrainteractive.astrarating.model.PluginTranslation

class ReloadCommand(
    private val permissionManager: PermissionManager,
    private val translation: PluginTranslation,
    private val translationContext: BukkitTranslationContext
) : Command, BukkitTranslationContext by translationContext {
    private val commandParser = ReloadCommandParser(
        alias = "aratingreload",
        permissionManager = permissionManager
    )

    class Input(val sender: CommandSender)

    override fun register(plugin: JavaPlugin) {
        Command.registerDefault(
            plugin = plugin,
            resultHandler = { commandSender, result ->
                commandSender.sendMessage(translation.noPermission)
            },
            commandParser = commandParser,
            commandExecutor = {
                it.sender.sendMessage(translation.reload)
                (plugin as AstraRating).reloadPlugin()
                it.sender.sendMessage(translation.reloadComplete)
            },
            transform = {
                (it as? ReloadCommandParser.Result.Success)?.sender?.let(ReloadCommand::Input)
            }
        )
    }
}
