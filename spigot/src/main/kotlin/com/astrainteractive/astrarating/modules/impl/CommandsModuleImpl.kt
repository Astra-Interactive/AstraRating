package com.astrainteractive.astrarating.modules.impl

import com.astrainteractive.astrarating.AstraRating
import com.astrainteractive.astrarating.commands.di.CommandsModule
import com.astrainteractive.astrarating.commands.rating.RatingCommandController
import com.astrainteractive.astrarating.gui.di.GuiModule
import com.astrainteractive.astrarating.gui.ratings.RatingsGUI
import com.astrainteractive.astrarating.modules.RootModule
import org.bukkit.entity.Player
import ru.astrainteractive.klibs.kdi.Factory
import ru.astrainteractive.klibs.kdi.Single
import ru.astrainteractive.klibs.kdi.getValue

object CommandsModuleImpl : CommandsModule {
    private val rootModule: RootModule by RootModuleImpl
    private val guiModule: GuiModule by GuiModuleImpl

    override val plugin: AstraRating by rootModule.plugin
    override val dispatchers by rootModule.dispatchers
    override val scope by rootModule.scope
    override val translation by rootModule.translation
    override val config by rootModule.config
    override val dbApi by rootModule.dbApi
    override val insertUseCase by rootModule.insertUserUseCase
    override fun ratingsGUIFactory(player: Player) = Factory {
        RatingsGUI(player, guiModule)
    }

    override val ratingCommandController by Single {
        RatingCommandController(this)
    }
}
