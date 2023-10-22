package ru.astrainteractive.astrarating.di.impl

import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.permission.BukkitPermissionManager
import ru.astrainteractive.astralibs.permission.PermissionManager
import ru.astrainteractive.astralibs.serialization.KyoriComponentSerializer
import ru.astrainteractive.astrarating.AstraRating
import ru.astrainteractive.astrarating.command.di.CommandsDependencies
import ru.astrainteractive.astrarating.command.rating.RatingCommandController
import ru.astrainteractive.astrarating.command.rating.exception.ValidationExceptionHandler
import ru.astrainteractive.astrarating.di.RootModule
import ru.astrainteractive.klibs.kdi.Factory
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.Single
import ru.astrainteractive.klibs.kdi.getValue

class CommandsDependenciesImpl(private val rootModule: RootModule) : CommandsDependencies {

    override val plugin: AstraRating by rootModule.servicesModule.plugin
    override val dispatchers by rootModule.servicesModule.dispatchers
    override val scope by rootModule.servicesModule.scope
    override val translation by rootModule.servicesModule.translation
    override val config by rootModule.servicesModule.config
    override val dbApi = rootModule.apiRatingModule.ratingDBApi
    override val insertUseCase = rootModule.apiRatingModule.insertUserUseCase
    override fun ratingsGUIFactory(player: Player) = Factory {
        rootModule.guiModule.playerRatingsGuiFactory(player).create()
    }

    override val permissionManager: PermissionManager = BukkitPermissionManager()
    override val translationContext: KyoriComponentSerializer by rootModule.servicesModule.componentSerializer
    override val validationExceptionHandler: ValidationExceptionHandler by Provider {
        ValidationExceptionHandler(translation, translationContext)
    }
    override val ratingCommandController by Single {
        RatingCommandController(this)
    }
}
