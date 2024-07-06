package ru.astrainteractive.astrarating.command.di

import kotlinx.coroutines.CoroutineScope
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astrarating.LifecyclePlugin
import ru.astrainteractive.astrarating.core.EmpireConfig
import ru.astrainteractive.astrarating.core.PluginTranslation
import ru.astrainteractive.astrarating.core.di.CoreModule
import ru.astrainteractive.astrarating.di.BukkitModule
import ru.astrainteractive.astrarating.feature.changerating.domain.usecase.AddRatingUseCase
import ru.astrainteractive.astrarating.feature.di.SharedModule
import ru.astrainteractive.astrarating.gui.di.GuiModule
import ru.astrainteractive.astrarating.gui.router.GuiRouter
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.getValue
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

internal interface CommandsDependencies {
    val plugin: LifecyclePlugin
    val addRatingUseCase: AddRatingUseCase
    val dispatchers: KotlinDispatchers
    val scope: CoroutineScope
    val translation: PluginTranslation
    val config: EmpireConfig
    val kyoriComponentSerializer: KyoriComponentSerializer
    val router: GuiRouter

    class Default(
        sharedModule: SharedModule,
        bukkitModule: BukkitModule,
        coreModule: CoreModule,
        guiModule: GuiModule
    ) : CommandsDependencies {

        override val plugin: LifecyclePlugin by bukkitModule.plugin
        override val addRatingUseCase: AddRatingUseCase by Provider {
            sharedModule.changeRatingModule.addRatingUseCase
        }
        override val dispatchers = coreModule.dispatchers
        override val scope by coreModule.scope
        override val translation by coreModule.translation
        override val config by coreModule.config

        override val kyoriComponentSerializer by bukkitModule.kyoriComponentSerializer

        override val router: GuiRouter by Provider { guiModule.router }
    }
}
