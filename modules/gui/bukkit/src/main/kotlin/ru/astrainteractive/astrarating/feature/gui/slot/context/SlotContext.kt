package ru.astrainteractive.astrarating.feature.gui.slot.context

import ru.astrainteractive.astralibs.menu.core.Menu
import ru.astrainteractive.astrarating.core.settings.AstraRatingConfig
import ru.astrainteractive.astrarating.core.settings.AstraRatingTranslation
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.getValue
import java.util.Locale

internal class SlotContext(
    translationKrate: CachedKrate<AstraRatingTranslation>,
    configKrate: CachedKrate<AstraRatingConfig>,
    val menu: Menu,
    /** Language of the player who opened the menu. */
    val locale: Locale
) {
    val translation by translationKrate
    val config by configKrate
}
