package ru.astrainteractive.astrarating.command.di

import kotlinx.coroutines.CoroutineScope
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.permission.PermissionManager
import ru.astrainteractive.astralibs.string.BukkitTranslationContext
import ru.astrainteractive.astrarating.AstraRating
import ru.astrainteractive.astrarating.feature.changerating.domain.usecase.AddRatingUseCase
import ru.astrainteractive.astrarating.gui.router.GuiRouter
import ru.astrainteractive.astrarating.model.EmpireConfig
import ru.astrainteractive.astrarating.model.PluginTranslation
import ru.astrainteractive.klibs.kdi.Module

interface CommandsDependencies : Module {
    val plugin: AstraRating
    val addRatingUseCase: AddRatingUseCase
    val dispatchers: BukkitDispatchers
    val scope: CoroutineScope
    val translation: PluginTranslation
    val config: EmpireConfig
    val permissionManager: PermissionManager
    val translationContext: BukkitTranslationContext
    val router: GuiRouter
}
