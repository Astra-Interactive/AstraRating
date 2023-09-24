package ru.astrainteractive.astrarating.gui.playerratings.di

import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astrarating.api.rating.api.RatingDBApi
import ru.astrainteractive.astrarating.gui.ratings.RatingsGUI
import ru.astrainteractive.astrarating.model.EmpireConfig
import ru.astrainteractive.astrarating.plugin.PluginTranslation
import ru.astrainteractive.klibs.kdi.Factory
import ru.astrainteractive.klibs.kdi.Module

interface PlayerRatingGuiModule : Module {
    val dbApi: RatingDBApi
    val dispatchers: BukkitDispatchers
    val translation: PluginTranslation
    val config: EmpireConfig
    fun playerRatingsGuiFactory(
        player: Player,
    ): Factory<RatingsGUI>
}
