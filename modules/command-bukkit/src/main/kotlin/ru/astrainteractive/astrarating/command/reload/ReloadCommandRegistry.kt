package ru.astrainteractive.astrarating.command.reload

import ru.astrainteractive.astralibs.command.api.commandfactory.BukkitCommandFactory
import ru.astrainteractive.astralibs.command.api.parser.BukkitCommandParser
import ru.astrainteractive.astralibs.command.api.registry.BukkitCommandRegistry
import ru.astrainteractive.astralibs.command.api.registry.BukkitCommandRegistryContext.Companion.toCommandRegistryContext
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.permission.BukkitPermissibleExt.toPermissible
import ru.astrainteractive.astrarating.LifecyclePlugin
import ru.astrainteractive.astrarating.core.PluginTranslation
import ru.astrainteractive.astrarating.core.RatingPermission

internal class ReloadCommandRegistry(
    private val plugin: LifecyclePlugin,
    private val translation: PluginTranslation,
    private val kyoriComponentSerializer: KyoriComponentSerializer
) : KyoriComponentSerializer by kyoriComponentSerializer {

    fun register() {
        val command = BukkitCommandFactory.create(
            alias = "aratingreload",
            commandParser = BukkitCommandParser { context ->
                val hasPermission = context.sender.toPermissible().hasPermission(RatingPermission.Reload)
                if (!hasPermission) return@BukkitCommandParser ReloadCommand.Result.NoPermission
                ReloadCommand.Result.Success(context.sender)
            },
            commandExecutor = {
                it.sender.sendMessage(translation.reload.let(::toComponent))
                plugin.onReload()
                it.sender.sendMessage(translation.reloadComplete.let(::toComponent))
            },
            commandSideEffect = { context, result ->
                when (result) {
                    ReloadCommand.Result.NoPermission -> {
                        context.sender.sendMessage(translation.noPermission.let(::toComponent))
                    }

                    is ReloadCommand.Result.Success -> Unit
                }
            },
            mapper = {
                (it as? ReloadCommand.Result.Success)?.sender?.let(ReloadCommand::Input)
            }
        )
        BukkitCommandRegistry.register(command, plugin.toCommandRegistryContext())
    }
}
