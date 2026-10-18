package ru.astrainteractive.astrarating.gui.bukkit.context

import ru.astrainteractive.astralibs.menu.core.Menu
import ru.astrainteractive.astrarating.core.settings.model.AstraRatingConfig
import ru.astrainteractive.astrarating.core.settings.model.AstraRatingTranslation
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
