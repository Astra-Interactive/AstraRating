package ru.astrainteractive.astrarating.command.di

import kotlinx.coroutines.CoroutineScope
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.string.BukkitTranslationContext
import ru.astrainteractive.astrarating.AstraRating
import ru.astrainteractive.astrarating.di.RootModule
import ru.astrainteractive.astrarating.feature.changerating.domain.usecase.AddRatingUseCase
import ru.astrainteractive.astrarating.gui.router.GuiRouter
import ru.astrainteractive.astrarating.model.EmpireConfig
import ru.astrainteractive.astrarating.model.PluginTranslation
import ru.astrainteractive.klibs.kdi.Module
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.getValue

interface CommandsDependencies : Module {
    val plugin: AstraRating
    val addRatingUseCase: AddRatingUseCase
    val dispatchers: BukkitDispatchers
    val scope: CoroutineScope
    val translation: PluginTranslation
    val config: EmpireConfig
    val translationContext: BukkitTranslationContext
    val router: GuiRouter

    class Default(rootModule: RootModule) : CommandsDependencies {

        override val plugin: AstraRating by rootModule.servicesModule.plugin
        override val addRatingUseCase: AddRatingUseCase by Provider {
            rootModule.sharedModule.changeRatingModule.addRatingUseCase
        }
        override val dispatchers by rootModule.servicesModule.dispatchers
        override val scope by rootModule.servicesModule.scope
        override val translation by rootModule.servicesModule.translation
        override val config by rootModule.servicesModule.config

        override val translationContext: BukkitTranslationContext by rootModule.servicesModule.translationContext

        override val router: GuiRouter by Provider { rootModule.guiModule.router }
    }
}
