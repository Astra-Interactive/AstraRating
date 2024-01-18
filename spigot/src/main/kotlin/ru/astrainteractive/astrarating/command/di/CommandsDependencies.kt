package ru.astrainteractive.astrarating.command.di

import kotlinx.coroutines.CoroutineScope
import ru.astrainteractive.astralibs.async.BukkitDispatchers
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

interface CommandsDependencies : Module {
    val plugin: AstraRating
    val addRatingUseCase: AddRatingUseCase
    val dispatchers: BukkitDispatchers
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
        override val dispatchers by rootModule.bukkitModule.dispatchers
        override val scope by rootModule.bukkitModule.scope
        override val translation by rootModule.bukkitModule.translation
        override val config by rootModule.bukkitModule.config

        override val kyoriComponentSerializer by rootModule.bukkitModule.kyoriComponentSerializer

        override val router: GuiRouter by Provider { rootModule.guiModule.router }
    }
}
