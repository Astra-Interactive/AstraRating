package ru.astrainteractive.astrarating.gui.bukkit.di

import ru.astrainteractive.astrarating.core.di.CoreModule
import ru.astrainteractive.astrarating.gui.api.di.GuiModule
import ru.astrainteractive.astrarating.gui.api.rating.player.di.RatingPlayerModule
import ru.astrainteractive.astrarating.gui.api.rating.ranking.di.RatingPlayersModule
import ru.astrainteractive.astrarating.gui.api.router.api.GuiRouter
import ru.astrainteractive.astrarating.gui.bukkit.router.BukkitGuiRouter

class GuiBukkitModule(
    coreModule: CoreModule,
    ratingPlayerModule: RatingPlayerModule,
    ratingPlayersModule: RatingPlayersModule
) : GuiModule {
    override val router: GuiRouter = BukkitGuiRouter(
        coreModule = coreModule,
        ratingPlayerModule = ratingPlayerModule,
        ratingPlayersModule = ratingPlayersModule
    )
}
