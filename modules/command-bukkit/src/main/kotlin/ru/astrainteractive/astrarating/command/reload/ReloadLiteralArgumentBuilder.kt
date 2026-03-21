package ru.astrainteractive.astrarating.command.reload

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.kyori.unwrap
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.server.KAudience
import ru.astrainteractive.astrarating.core.settings.AstraRatingTranslation
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.util.getValue
import ru.astrainteractive.klibs.mikro.core.util.tryCast

internal class ReloadLiteralArgumentBuilder(
    private val kyoriKrate: CachedKrate<KyoriComponentSerializer>,
    private val lifecyclePlugin: Lifecycle,
    private val multiplatformCommand: MultiplatformCommand,
    translationKrate: CachedKrate<AstraRatingTranslation>,
) : KyoriComponentSerializer by kyoriKrate.unwrap() {
    private val translation by translationKrate

    fun create(): LiteralArgumentBuilder<Any> {
        return with(multiplatformCommand) {
            command("aratingreload") {
                runs { ctx ->
                    ctx.getSender()
                        .tryCast<KAudience>()
                        ?.sendMessage(translation.general.reload.component)
                    lifecyclePlugin.onReload()
                    ctx.getSender()
                        .tryCast<KAudience>()
                        ?.sendMessage(translation.general.reloadComplete.component)
                }
            }
        }
    }
}
