package ru.astrainteractive.astrarating.gui.di

import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astrarating.api.rating.di.ApiRatingModule
import ru.astrainteractive.astrarating.core.di.CoreModule
import ru.astrainteractive.astrarating.gui.playerratings.di.PlayerRatingGuiDependencies
import ru.astrainteractive.astrarating.gui.ratings.di.RatingsGUIDependencies

internal interface GuiDependencies : PlayerRatingGuiDependencies, RatingsGUIDependencies {
    class Default(
        apiRatingModule: ApiRatingModule,
        private val coreModule: CoreModule,
        override val translationContext: KyoriComponentSerializer
    ) : GuiDependencies {

        override val dbApi = apiRatingModule.ratingDBApi
        override val dispatchers = coreModule.dispatchers
        override val translation get() = coreModule.translation.cachedValue
        override val config get() = coreModule.config.cachedValue
    }
}
