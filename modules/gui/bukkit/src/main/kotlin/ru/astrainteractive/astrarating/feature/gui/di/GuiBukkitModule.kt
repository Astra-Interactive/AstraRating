package ru.astrainteractive.astrarating.feature.gui.di

import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astrarating.core.di.CoreModule
import ru.astrainteractive.astrarating.feature.gui.router.GuiRouter
import ru.astrainteractive.astrarating.feature.gui.router.GuiRouterImpl
import ru.astrainteractive.astrarating.feature.rating.players.di.RatingPlayersModule
import ru.astrainteractive.astrarating.feature.ratings.player.di.RatingPlayerModule
import ru.astrainteractive.klibs.kstorage.api.CachedKrate

class GuiBukkitModule(
    coreModule: CoreModule,
    translationContext: CachedKrate<KyoriComponentSerializer>,
    ratingPlayerModule: RatingPlayerModule,
    ratingPlayersModule: RatingPlayersModule
) : GuiModule {
    override val router: GuiRouter = GuiRouterImpl(
        coreModule = coreModule,
        translationContext = translationContext,
        ratingPlayerModule = ratingPlayerModule,
        ratingPlayersModule = ratingPlayersModule
    )
}
