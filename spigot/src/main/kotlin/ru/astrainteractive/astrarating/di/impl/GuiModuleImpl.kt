package ru.astrainteractive.astrarating.di.impl

import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.permission.BukkitPermissionManager
import ru.astrainteractive.astralibs.permission.PermissionManager
import ru.astrainteractive.astralibs.serialization.KyoriComponentSerializer
import ru.astrainteractive.astrarating.di.RootModule
import ru.astrainteractive.astrarating.gui.di.GuiModule
import ru.astrainteractive.astrarating.gui.playerratings.PlayerRatingsGUI
import ru.astrainteractive.astrarating.gui.ratings.RatingsGUI
import ru.astrainteractive.astrarating.model.PlayerModel
import ru.astrainteractive.klibs.kdi.Factory
import ru.astrainteractive.klibs.kdi.getValue

class GuiModuleImpl(private val rootModule: RootModule) : GuiModule {

    override val dbApi = rootModule.apiRatingModule.ratingDBApi
    override val dispatchers by rootModule.servicesModule.dispatchers
    override val translation by rootModule.servicesModule.translation
    override val config by rootModule.servicesModule.config
    override val permissionManager: PermissionManager = BukkitPermissionManager()
    override val translationContext: KyoriComponentSerializer by rootModule.servicesModule.componentSerializer

    override fun playerRatingsGuiFactory(
        player: Player
    ): Factory<RatingsGUI> = Factory {
        RatingsGUI(
            player = player,
            module = this,
            allRatingsComponent = rootModule.sharedModule.allRatingsComponentFactory().create()
        )
    }

    override fun allRatingsGuiFactory(
        selectedPlayer: OfflinePlayer,
        player: Player
    ): Factory<PlayerRatingsGUI> = Factory {
        PlayerRatingsGUI(
            selectedPlayer = selectedPlayer,
            player = player,
            module = this,
            playerRatingsComponent = rootModule.sharedModule.playerRatingsComponentFactory(
                playerModel = PlayerModel(
                    uuid = selectedPlayer.uniqueId,
                    name = selectedPlayer.name ?: selectedPlayer.uniqueId.toString()
                )
            ).create()
        )
    }
}
