package com.astrainteractive.astrarating.gui.playerratings.di

import com.astrainteractive.astrarating.domain.api.RatingDBApi
import com.astrainteractive.astrarating.gui.ratings.RatingsGUI
import com.astrainteractive.astrarating.plugin.EmpireConfig
import com.astrainteractive.astrarating.plugin.PluginTranslation
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.Dependency
import ru.astrainteractive.astralibs.Factory
import ru.astrainteractive.astralibs.Module
import ru.astrainteractive.astralibs.async.BukkitDispatchers

interface PlayerRatingGuiModule : Module {
    val dbApi: Dependency<RatingDBApi>
    val dispatchers: Dependency<BukkitDispatchers>
    val translation: Dependency<PluginTranslation>
    val config: Dependency<EmpireConfig>
    fun playerRatingsGuiFactory(
        player: Player,
    ): Factory<RatingsGUI>
}
