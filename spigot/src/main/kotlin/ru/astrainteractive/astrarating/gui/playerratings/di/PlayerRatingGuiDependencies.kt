package ru.astrainteractive.astrarating.gui.playerratings.di

import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.serialization.KyoriComponentSerializer
import ru.astrainteractive.astrarating.api.rating.api.RatingDBApi
import ru.astrainteractive.astrarating.model.EmpireConfig
import ru.astrainteractive.astrarating.model.PluginTranslation
import ru.astrainteractive.klibs.kdi.Module

interface PlayerRatingGuiDependencies : Module {
    val dbApi: RatingDBApi
    val dispatchers: BukkitDispatchers
    val translation: PluginTranslation
    val config: EmpireConfig
    val translationContext: KyoriComponentSerializer
}
