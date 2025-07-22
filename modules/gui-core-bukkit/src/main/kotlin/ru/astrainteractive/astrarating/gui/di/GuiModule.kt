package ru.astrainteractive.astrarating.gui.di

import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astrarating.api.rating.di.ApiRatingModule
import ru.astrainteractive.astrarating.core.di.CoreModule
import ru.astrainteractive.astrarating.feature.allrating.di.AllRatingsModule
import ru.astrainteractive.astrarating.feature.playerrating.di.PlayerRatingsModule
import ru.astrainteractive.astrarating.gui.router.GuiRouter
import ru.astrainteractive.astrarating.gui.router.GuiRouterImpl

interface GuiModule {
    val router: GuiRouter

    class Default(
        coreModule: CoreModule,
        apiRatingModule: ApiRatingModule,
        translationContext: KyoriComponentSerializer,
        playerRatingsModule: PlayerRatingsModule,
        allRatingsModule: AllRatingsModule
    ) : GuiModule {
        override val router: GuiRouter = GuiRouterImpl(
            coreModule = coreModule,
            apiRatingModule = apiRatingModule,
            translationContext = translationContext,
            playerRatingsModule = playerRatingsModule,
            allRatingsModule = allRatingsModule
        )
    }
}
