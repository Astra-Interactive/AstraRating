package ru.astrainteractive.astrarating.gui.di

import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astrarating.api.rating.di.ApiRatingModule
import ru.astrainteractive.astrarating.core.di.CoreModule
import ru.astrainteractive.astrarating.gui.playerratings.di.PlayerRatingGuiDependencies
import ru.astrainteractive.astrarating.gui.ratings.di.RatingsGUIDependencies
import ru.astrainteractive.klibs.kdi.getValue

internal interface GuiDependencies : PlayerRatingGuiDependencies, RatingsGUIDependencies {
    class Default(
        apiRatingModule: ApiRatingModule,
        coreModule: CoreModule,
        override val translationContext: KyoriComponentSerializer
    ) : GuiDependencies {

        override val dbApi = apiRatingModule.ratingDBApi
        override val dispatchers = coreModule.dispatchers
        override val translation by coreModule.translation
        override val config by coreModule.config
    }
}
