package ru.astrainteractive.astrarating.gui.slot.context

import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.menu.core.Menu
import ru.astrainteractive.astrarating.core.EmpireConfig
import ru.astrainteractive.astrarating.core.PluginTranslation

internal interface SlotContext : KyoriComponentSerializer {
    val translation: PluginTranslation
    val config: EmpireConfig
    val menu: Menu
}
