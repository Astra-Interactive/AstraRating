package ru.astrainteractive.astrarating.command.reload

import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import ru.astrainteractive.astralibs.command.api.util.command
import ru.astrainteractive.astralibs.command.api.util.runs
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.lifecycle.LifecyclePlugin
import ru.astrainteractive.astrarating.core.settings.AstraRatingTranslation
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.util.getValue

fun createReloadCommandNode(
    lifecyclePlugin: LifecyclePlugin,
    translationKrate: CachedKrate<AstraRatingTranslation>,
    kyoriKrate: CachedKrate<KyoriComponentSerializer>
): LiteralCommandNode<CommandSourceStack> {
    val translation by translationKrate
    val kyori by kyoriKrate
    return with(kyori) {
        command("aratingreload") {
            runs {
                it.source.sender.sendMessage(translation.general.reload.component)
                lifecyclePlugin.onReload()
                it.source.sender.sendMessage(translation.general.reloadComplete.component)
            }
        }.build()
    }
}
