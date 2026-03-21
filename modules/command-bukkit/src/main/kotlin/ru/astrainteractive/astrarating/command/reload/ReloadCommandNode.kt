package ru.astrainteractive.astrarating.command.reload

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.server.KAudience
import ru.astrainteractive.astrarating.core.settings.AstraRatingTranslation
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.util.getValue
import ru.astrainteractive.klibs.mikro.core.util.tryCast

fun createReloadCommandNode(
    kyoriKrate: CachedKrate<KyoriComponentSerializer>,
    lifecyclePlugin: Lifecycle,
    multiplatformCommand: MultiplatformCommand,
    translationKrate: CachedKrate<AstraRatingTranslation>,
): LiteralArgumentBuilder<*> {
    val translation by translationKrate
    val kyori by kyoriKrate
    return with(kyori) {
        with(multiplatformCommand) {
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
