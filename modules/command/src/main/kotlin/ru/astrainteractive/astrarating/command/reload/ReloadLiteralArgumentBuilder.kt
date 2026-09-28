package ru.astrainteractive.astrarating.command.reload

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astrarating.core.command.CommandExceptionHandler
import ru.astrainteractive.astrarating.core.settings.AstraRatingPermission
import ru.astrainteractive.astrarating.core.settings.AstraRatingTranslation
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.getValue

internal class ReloadLiteralArgumentBuilder(
    private val lifecyclePlugin: Lifecycle,
    private val multiplatformCommand: MultiplatformCommand,
    private val commandExceptionHandler: CommandExceptionHandler,
    translationKrate: CachedKrate<AstraRatingTranslation>,
) {
    private val translation by translationKrate

    fun create(): LiteralArgumentBuilder<Any> {
        return with(multiplatformCommand) {
            command("aratingreload") {
                runs(commandExceptionHandler::handle) { ctx ->
                    ctx.requirePermission(AstraRatingPermission.Reload)
                    ctx.getSender().sendMessage(translation.reload.started)
                    lifecyclePlugin.onReload()
                    ctx.getSender().sendMessage(translation.reload.completed)
                }
            }
        }
    }
}
