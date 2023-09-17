package com.astrainteractive.astrarating.modules.impl

import com.astrainteractive.astrarating.gui.di.GuiModule
import com.astrainteractive.astrarating.gui.playerratings.PlayerRatingsGUI
import com.astrainteractive.astrarating.gui.ratings.RatingsGUI
import com.astrainteractive.astrarating.modules.RootModule
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import ru.astrainteractive.klibs.kdi.Factory
import ru.astrainteractive.klibs.kdi.getValue

object GuiModuleImpl : GuiModule {
    private val rootModule: RootModule by RootModuleImpl

    override val dbApi by rootModule.dbApi
    override val dispatchers by rootModule.dispatchers
    override val translation by rootModule.translation
    override val config by rootModule.config

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
