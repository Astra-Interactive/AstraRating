package ru.astrainteractive.astrarating.gui.slot

import ru.astrainteractive.astrarating.core.EmpireConfig
import ru.astrainteractive.astrarating.core.PluginTranslation

interface SlotContext {
    val translation: PluginTranslation
    val config: EmpireConfig
}
