package ru.astrainteractive.astrarating.command.reload

import ru.astrainteractive.astralibs.command.api.context.BukkitCommandContextExt.requirePermission
import ru.astrainteractive.astralibs.command.api.error.ErrorHandler
import ru.astrainteractive.astralibs.command.api.exception.DefaultCommandException
import ru.astrainteractive.astralibs.command.api.executor.CommandExecutor
import ru.astrainteractive.astralibs.command.api.parser.CommandParser
import ru.astrainteractive.astralibs.command.api.util.PluginExt.registerCommand
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astrarating.LifecyclePlugin
import ru.astrainteractive.astrarating.core.PluginTranslation
import ru.astrainteractive.astrarating.core.RatingPermission

internal class ReloadCommandRegistry(
    private val plugin: LifecyclePlugin,
    private val translation: PluginTranslation,
    private val kyoriComponentSerializer: KyoriComponentSerializer
) : KyoriComponentSerializer by kyoriComponentSerializer {

    fun register() {
        plugin.registerCommand(
            alias = "aratingreload",
            commandExecutor = CommandExecutor<ReloadCommand.Result> {
                it.sender.sendMessage(translation.reload.let(::toComponent))
                plugin.onReload()
                it.sender.sendMessage(translation.reloadComplete.let(::toComponent))
            },
            commandParser = CommandParser { context ->
                context.requirePermission(RatingPermission.Reload)
                ReloadCommand.Result(context.sender)
            },
            errorHandler = ErrorHandler { commandContext, throwable ->
                when (throwable) {
                    is DefaultCommandException.NoPermissionException -> with(kyoriComponentSerializer) {
                        commandContext.sender.sendMessage(translation.noPermission.component)
                    }
                }
            }
        )
    }
}
