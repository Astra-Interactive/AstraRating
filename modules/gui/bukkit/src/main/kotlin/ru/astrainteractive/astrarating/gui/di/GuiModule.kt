package ru.astrainteractive.astrarating.gui.di

import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astrarating.api.rating.di.ApiRatingModule
import ru.astrainteractive.astrarating.core.di.CoreModule
import ru.astrainteractive.astrarating.feature.di.SharedModule
import ru.astrainteractive.astrarating.gui.router.GuiRouter
import ru.astrainteractive.astrarating.gui.router.GuiRouterImpl
import ru.astrainteractive.klibs.kstorage.api.CachedKrate

interface GuiModule {
    val router: GuiRouter

    class Default(
        coreModule: CoreModule,
        apiRatingModule: ApiRatingModule,
        kyoriKrate: CachedKrate<KyoriComponentSerializer>,
        sharedModule: SharedModule
    ) : GuiModule {
        override val router: GuiRouter = GuiRouterImpl(
            coreModule = coreModule,
            apiRatingModule = apiRatingModule,
            kyoriKrate = kyoriKrate,
            sharedModule = sharedModule
        )
    }
}
