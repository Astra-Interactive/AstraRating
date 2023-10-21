package ru.astrainteractive.astrarating.di.impl

import org.bukkit.entity.Player
import ru.astrainteractive.astrarating.AstraRating
import ru.astrainteractive.astrarating.command.di.CommandsModule
import ru.astrainteractive.astrarating.command.rating.RatingCommandController
import ru.astrainteractive.astrarating.di.RootModule
import ru.astrainteractive.astrarating.gui.ratings.RatingsGUI
import ru.astrainteractive.klibs.kdi.Factory
import ru.astrainteractive.klibs.kdi.Single
import ru.astrainteractive.klibs.kdi.getValue

class CommandsModuleImpl(private val rootModule: RootModule) : CommandsModule {

    override val plugin: AstraRating by rootModule.servicesModule.plugin
    override val dispatchers by rootModule.servicesModule.dispatchers
    override val scope by rootModule.servicesModule.scope
    override val translation by rootModule.servicesModule.translation
    override val config by rootModule.servicesModule.config
    override val dbApi = rootModule.apiRatingModule.ratingDBApi
    override val insertUseCase = rootModule.apiRatingModule.insertUserUseCase
    override fun ratingsGUIFactory(player: Player) = Factory {
        RatingsGUI(player, rootModule.guiModule)
    }

    override val ratingCommandController by Single {
        RatingCommandController(this)
    }
}
