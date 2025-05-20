package ru.astrainteractive.astrarating.gui.util

import ru.astrainteractive.astralibs.string.StringDesc
import ru.astrainteractive.astrarating.core.PluginTranslation

internal class TranslationDesc(private val provider: (PluginTranslation) -> StringDesc.Raw) {
    fun toString(pluginTranslation: PluginTranslation): StringDesc.Raw = provider.invoke(pluginTranslation)
}
