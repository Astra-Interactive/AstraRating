package ru.astrainteractive.astrarating.gui.di

import ru.astrainteractive.astralibs.serialization.KyoriComponentSerializer
import ru.astrainteractive.astrarating.di.RootModule
import ru.astrainteractive.astrarating.gui.playerratings.di.PlayerRatingGuiDependencies
import ru.astrainteractive.astrarating.gui.ratings.di.RatingsGUIDependencies
import ru.astrainteractive.klibs.kdi.getValue

interface GuiDependencies : PlayerRatingGuiDependencies, RatingsGUIDependencies {
    class Default(rootModule: RootModule) : GuiDependencies {

        override val dbApi = rootModule.apiRatingModule.ratingDBApi
        override val dispatchers by rootModule.servicesModule.dispatchers
        override val translation by rootModule.servicesModule.translation
        override val config by rootModule.servicesModule.config
        override val translationContext: KyoriComponentSerializer by rootModule.servicesModule.componentSerializer
    }
}
