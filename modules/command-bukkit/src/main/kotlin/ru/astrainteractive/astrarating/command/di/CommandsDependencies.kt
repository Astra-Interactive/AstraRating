package ru.astrainteractive.astrarating.command.di

import kotlinx.coroutines.CoroutineScope
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.lifecycle.LifecyclePlugin
import ru.astrainteractive.astrarating.core.EmpireConfig
import ru.astrainteractive.astrarating.core.PluginTranslation
import ru.astrainteractive.astrarating.core.di.CoreModule
import ru.astrainteractive.astrarating.di.BukkitModule
import ru.astrainteractive.astrarating.feature.changerating.di.ChangeRatingModule
import ru.astrainteractive.astrarating.feature.changerating.domain.usecase.AddRatingUseCase
import ru.astrainteractive.astrarating.gui.di.GuiModule
import ru.astrainteractive.astrarating.gui.router.GuiRouter
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
        private val changeRatingModule: ChangeRatingModule,
        private val bukkitModule: BukkitModule,
        private val coreModule: CoreModule,
        private val guiModule: GuiModule
    ) : CommandsDependencies {

        override val plugin: LifecyclePlugin = bukkitModule.plugin
        override val addRatingUseCase: AddRatingUseCase = changeRatingModule.addRatingUseCase
        override val dispatchers = coreModule.dispatchers
        override val scope = coreModule.scope
        override val translation get() = coreModule.translation.cachedValue
        override val config get() = coreModule.config.cachedValue

        override val kyoriComponentSerializer get() = bukkitModule.kyoriComponentSerializer.cachedValue

        override val router: GuiRouter get() = guiModule.router
    }
}
