package ru.astrainteractive.astrarating.command.reload

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.kyori.unwrap
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astrarating.core.settings.AstraRatingTranslation
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.util.getValue

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
                    ctx.getSender().sendMessage(translation.general.reload.component)
                    lifecyclePlugin.onReload()
                    ctx.getSender().sendMessage(translation.general.reloadComplete.component)
                }
            }
        }
    }
}
