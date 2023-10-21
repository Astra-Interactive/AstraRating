package ru.astrainteractive.astrarating.gui.util

import ru.astrainteractive.astrarating.plugin.PluginTranslation

class TranslationDesc(private val provider: (PluginTranslation) -> String) {
    fun toString(pluginTranslation: PluginTranslation) = provider.invoke(pluginTranslation)
}
