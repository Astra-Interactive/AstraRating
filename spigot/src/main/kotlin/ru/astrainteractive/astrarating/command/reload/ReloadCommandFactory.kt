package ru.astrainteractive.astrarating.command.reload

import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.astralibs.command.api.Command
import ru.astrainteractive.astralibs.command.api.CommandParser
import ru.astrainteractive.astralibs.command.api.DefaultCommandFactory
import ru.astrainteractive.astralibs.permission.BukkitPermissibleExt.toPermissible
import ru.astrainteractive.astralibs.string.BukkitTranslationContext
import ru.astrainteractive.astrarating.AstraRating
import ru.astrainteractive.astrarating.model.PluginTranslation
import ru.astrainteractive.astrarating.plugin.RatingPermission
import ru.astrainteractive.klibs.kdi.Factory

class ReloadCommandFactory(
    private val plugin: JavaPlugin,
    private val translation: PluginTranslation,
    private val translationContext: BukkitTranslationContext
) : Factory<ReloadCommand>, BukkitTranslationContext by translationContext {

    private inner class ReloadCommandImpl :
        ReloadCommand,
        Command<ReloadCommand.Result, ReloadCommand.Input> by DefaultCommandFactory.create(
            alias = "aratingreload",
            commandParser = CommandParser { _, sender ->
                val hasPermission = sender.toPermissible().hasPermission(RatingPermission.Reload)
                if (!hasPermission) return@CommandParser ReloadCommand.Result.NoPermission
                return@CommandParser ReloadCommand.Result.Success(sender)
            },
            commandExecutor = {
                it.sender.sendMessage(translation.reload)
                (plugin as AstraRating).reloadPlugin()
                it.sender.sendMessage(translation.reloadComplete)
            },
            resultHandler = { commandSender, result ->
                when (result) {
                    ReloadCommand.Result.NoPermission -> {
                        commandSender.sendMessage(translation.noPermission)
                    }

                    is ReloadCommand.Result.Success -> Unit
                }
            },
            mapper = {
                (it as? ReloadCommand.Result.Success)?.sender?.let(ReloadCommand::Input)
            }
        )

    override fun create(): ReloadCommand {
        return ReloadCommandImpl().also {
            it.register(plugin)
        }
    }
}
