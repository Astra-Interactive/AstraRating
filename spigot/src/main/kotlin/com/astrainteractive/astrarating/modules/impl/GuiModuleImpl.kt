package com.astrainteractive.astrarating.modules.impl

import com.astrainteractive.astrarating.domain.api.RatingDBApi
import com.astrainteractive.astrarating.gui.di.GuiModule
import com.astrainteractive.astrarating.gui.playerratings.PlayerRatingsGUI
import com.astrainteractive.astrarating.gui.ratings.RatingsGUI
import com.astrainteractive.astrarating.modules.RootModule
import com.astrainteractive.astrarating.plugin.EmpireConfig
import com.astrainteractive.astrarating.plugin.PluginTranslation
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.Dependency
import ru.astrainteractive.astralibs.Factory
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.getValue

object GuiModuleImpl : GuiModule {
    private val rootModule: RootModule by RootModuleImpl

    override val dbApi: Dependency<RatingDBApi> = rootModule.dbApi
    override val dispatchers: Dependency<BukkitDispatchers> = rootModule.dispatchers
    override val translation: Dependency<PluginTranslation> = rootModule.translation
    override val config: Dependency<EmpireConfig> = rootModule.config

    override fun playerRatingsGuiFactory(
        player: Player
    ): Factory<RatingsGUI> = Factory {
        RatingsGUI(
            player,
            this
        )
    }

    override fun playerRatingsGuiFactory(
        selectedPlayer: OfflinePlayer,
        player: Player
    ): Factory<PlayerRatingsGUI> = Factory {
        PlayerRatingsGUI(
            selectedPlayer,
            player,
            this
        )
    }
}
