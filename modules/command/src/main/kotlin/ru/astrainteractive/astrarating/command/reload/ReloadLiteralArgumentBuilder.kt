package ru.astrainteractive.astrarating.command.reload

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astrarating.core.settings.AstraRatingTranslation
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.getValue

internal class ReloadLiteralArgumentBuilder(
    private val lifecyclePlugin: Lifecycle,
    private val multiplatformCommand: MultiplatformCommand,
    translationKrate: CachedKrate<AstraRatingTranslation>,
) {
    private val translation by translationKrate

    fun create(): LiteralArgumentBuilder<Any> {
        return with(multiplatformCommand) {
            command("aratingreload") {
                runs { ctx ->
                    ctx.getSender().sendMessage(translation.general.reload)
                    lifecyclePlugin.onReload()
                    ctx.getSender().sendMessage(translation.general.reloadComplete)
                }
            }
        }
    }
}
