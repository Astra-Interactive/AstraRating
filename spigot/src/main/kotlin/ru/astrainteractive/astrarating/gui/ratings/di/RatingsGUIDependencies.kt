package ru.astrainteractive.astrarating.gui.ratings.di

import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astrarating.api.rating.api.RatingDBApi
import ru.astrainteractive.astrarating.core.EmpireConfig
import ru.astrainteractive.astrarating.core.PluginTranslation
import ru.astrainteractive.klibs.kdi.Module
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

interface RatingsGUIDependencies : Module {
    val dbApi: RatingDBApi
    val dispatchers: KotlinDispatchers
    val translation: PluginTranslation
    val config: EmpireConfig
    val translationContext: KyoriComponentSerializer
}
