package ru.astrainteractive.astrarating.gui.di

import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astrarating.core.di.CoreModule
import ru.astrainteractive.astrarating.feature.rating.players.di.RatingPlayersModule
import ru.astrainteractive.astrarating.feature.ratings.player.di.RatingPlayerModule
import ru.astrainteractive.astrarating.gui.router.GuiRouter
import ru.astrainteractive.astrarating.gui.router.GuiRouterImpl
import ru.astrainteractive.klibs.kstorage.api.CachedKrate

class GuiModule(
    coreModule: CoreModule,
    translationContext: CachedKrate<KyoriComponentSerializer>,
    ratingPlayerModule: RatingPlayerModule,
    ratingPlayersModule: RatingPlayersModule
) {
    val router: GuiRouter = GuiRouterImpl(
        coreModule = coreModule,
        translationContext = translationContext,
        ratingPlayerModule = ratingPlayerModule,
        ratingPlayersModule = ratingPlayersModule
    )
}
