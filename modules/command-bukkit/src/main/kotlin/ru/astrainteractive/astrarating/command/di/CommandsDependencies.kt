package ru.astrainteractive.astrarating.command.di

import kotlinx.coroutines.CoroutineScope
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.lifecycle.LifecyclePlugin
import ru.astrainteractive.astrarating.core.di.BukkitModule
import ru.astrainteractive.astrarating.core.di.CoreModule
import ru.astrainteractive.astrarating.core.settings.AstraRatingConfig
import ru.astrainteractive.astrarating.core.settings.AstraRatingTranslation
import ru.astrainteractive.astrarating.feature.rating.change.di.RatingChangeModule
import ru.astrainteractive.astrarating.feature.rating.change.domain.usecase.AddRatingUseCase
import ru.astrainteractive.astrarating.gui.di.GuiModule
import ru.astrainteractive.astrarating.gui.router.GuiRouter
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

internal interface CommandsDependencies {
    val plugin: LifecyclePlugin
    val addRatingUseCase: AddRatingUseCase
    val dispatchers: KotlinDispatchers
    val scope: CoroutineScope
    val translation: AstraRatingTranslation
    val config: AstraRatingConfig
    val kyoriComponentSerializer: KyoriComponentSerializer
    val router: GuiRouter

    class Default(
        private val ratingChangeModule: RatingChangeModule,
        private val bukkitModule: BukkitModule,
        private val coreModule: CoreModule,
        private val guiModule: GuiModule
    ) : CommandsDependencies {

        override val plugin: LifecyclePlugin = bukkitModule.plugin
        override val addRatingUseCase: AddRatingUseCase = ratingChangeModule.addRatingUseCase
        override val dispatchers = coreModule.dispatchers
        override val scope = coreModule.ioScope
        override val translation get() = coreModule.translationKrate.cachedValue
        override val config get() = coreModule.configKrate.cachedValue

        override val kyoriComponentSerializer get() = bukkitModule.kyoriKrate.cachedValue

        override val router: GuiRouter get() = guiModule.router
    }
}
