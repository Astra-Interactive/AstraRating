package ru.astrainteractive.astrarating.command.di

import ru.astrainteractive.astralibs.permission.BukkitPermissionManager
import ru.astrainteractive.astralibs.permission.PermissionManager
import ru.astrainteractive.astralibs.string.BukkitTranslationContext
import ru.astrainteractive.astrarating.AstraRating
import ru.astrainteractive.astrarating.di.RootModule
import ru.astrainteractive.astrarating.feature.changerating.domain.usecase.AddRatingUseCase
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.getValue

class CommandsDependenciesImpl(private val rootModule: RootModule) : CommandsDependencies {

    override val plugin: AstraRating by rootModule.servicesModule.plugin
    override val addRatingUseCase: AddRatingUseCase by Provider {
        rootModule.sharedModule.changeRatingModule.addRatingUseCase
    }
    override val dispatchers by rootModule.servicesModule.dispatchers
    override val scope by rootModule.servicesModule.scope
    override val translation by rootModule.servicesModule.translation
    override val config by rootModule.servicesModule.config

    override val permissionManager: PermissionManager = BukkitPermissionManager()
    override val translationContext: BukkitTranslationContext by rootModule.servicesModule.translationContext
}
