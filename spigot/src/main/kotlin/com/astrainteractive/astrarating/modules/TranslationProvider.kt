package com.astrainteractive.astrarating.modules

import com.astrainteractive.astrarating.utils.PluginTranslation
import ru.astrainteractive.astralibs.di.IReloadable

object TranslationProvider : IReloadable<PluginTranslation>() {
    override fun initializer(): PluginTranslation {
        return PluginTranslation()
    }
}