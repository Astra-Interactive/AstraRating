package ru.astrainteractive.astrarating.gui.slot

import ru.astrainteractive.astrarating.model.EmpireConfig
import ru.astrainteractive.astrarating.plugin.PluginTranslation

interface SlotContext {
    val translation: PluginTranslation
    val config: EmpireConfig
}
