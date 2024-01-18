package ru.astrainteractive.astrarating.command.di

import kotlinx.coroutines.CoroutineScope
import ru.astrainteractive.astralibs.serialization.KyoriComponentSerializer
import ru.astrainteractive.astrarating.AstraRating
import ru.astrainteractive.astrarating.core.EmpireConfig
import ru.astrainteractive.astrarating.core.PluginTranslation
import ru.astrainteractive.astrarating.di.RootModule
import ru.astrainteractive.astrarating.feature.changerating.domain.usecase.AddRatingUseCase
import ru.astrainteractive.astrarating.gui.router.GuiRouter
import ru.astrainteractive.klibs.kdi.Module
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.getValue
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

interface CommandsDependencies : Module {
    val plugin: AstraRating
    val addRatingUseCase: AddRatingUseCase
    val dispatchers: KotlinDispatchers
    val scope: CoroutineScope
    val translation: PluginTranslation
    val config: EmpireConfig
    val kyoriComponentSerializer: KyoriComponentSerializer
    val router: GuiRouter

    class Default(rootModule: RootModule) : CommandsDependencies {

        override val plugin: AstraRating by rootModule.bukkitModule.plugin
        override val addRatingUseCase: AddRatingUseCase by Provider {
            rootModule.sharedModule.changeRatingModule.addRatingUseCase
        }
        override val dispatchers = rootModule.coreModule.dispatchers
        override val scope by rootModule.coreModule.scope
        override val translation by rootModule.coreModule.translation
        override val config by rootModule.coreModule.config

        override val kyoriComponentSerializer by rootModule.bukkitModule.kyoriComponentSerializer

        override val router: GuiRouter by Provider { rootModule.guiModule.router }
    }
}
