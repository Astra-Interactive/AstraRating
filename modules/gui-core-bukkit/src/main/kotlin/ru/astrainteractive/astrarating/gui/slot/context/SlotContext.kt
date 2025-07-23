package ru.astrainteractive.astrarating.gui.slot.context

import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.kyori.unwrap
import ru.astrainteractive.astralibs.menu.core.Menu
import ru.astrainteractive.astrarating.core.settings.AstraRatingConfig
import ru.astrainteractive.astrarating.core.settings.AstraRatingTranslation
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.util.getValue

internal class SlotContext(
    translationKrate: CachedKrate<AstraRatingTranslation>,
    configKrate: CachedKrate<AstraRatingConfig>,
    kyoriKrate: CachedKrate<KyoriComponentSerializer>,
    val menu: Menu
) : KyoriComponentSerializer by kyoriKrate.unwrap() {
    val translation by translationKrate
    val config by configKrate
}
