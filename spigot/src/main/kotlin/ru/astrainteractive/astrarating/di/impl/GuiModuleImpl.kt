package ru.astrainteractive.astrarating.di.impl

import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import ru.astrainteractive.astrarating.di.RootModule
import ru.astrainteractive.astrarating.gui.di.GuiModule
import ru.astrainteractive.astrarating.gui.playerratings.PlayerRatingsGUI
import ru.astrainteractive.astrarating.gui.ratings.RatingsGUI
import ru.astrainteractive.klibs.kdi.Factory
import ru.astrainteractive.klibs.kdi.getValue

class GuiModuleImpl(rootModule: RootModule) : GuiModule {

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

    override fun allRatingsGuiFactory(
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
