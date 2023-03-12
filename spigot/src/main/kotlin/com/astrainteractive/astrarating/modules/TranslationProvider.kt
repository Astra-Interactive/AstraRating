package com.astrainteractive.astrarating.modules

import com.astrainteractive.astrarating.utils.PluginTranslation
import ru.astrainteractive.astralibs.di.Reloadable

object TranslationProvider : Reloadable<PluginTranslation>() {
    override fun initializer(): PluginTranslation {
        return PluginTranslation()
    }
}