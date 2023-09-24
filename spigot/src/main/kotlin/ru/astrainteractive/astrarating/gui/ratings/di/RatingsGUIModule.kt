package ru.astrainteractive.astrarating.gui.ratings.di

import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astrarating.api.rating.api.RatingDBApi
import ru.astrainteractive.astrarating.gui.playerratings.PlayerRatingsGUI
import ru.astrainteractive.astrarating.model.EmpireConfig
import ru.astrainteractive.astrarating.plugin.PluginTranslation
import ru.astrainteractive.klibs.kdi.Factory
import ru.astrainteractive.klibs.kdi.Module

interface RatingsGUIModule : Module {
    val dbApi: RatingDBApi
    val dispatchers: BukkitDispatchers
    val translation: PluginTranslation
    val config: EmpireConfig
    fun playerRatingsGuiFactory(
        selectedPlayer: OfflinePlayer,
        player: Player,
    ): Factory<PlayerRatingsGUI>
}
