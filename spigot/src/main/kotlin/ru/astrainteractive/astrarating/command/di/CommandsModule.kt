package ru.astrainteractive.astrarating.command.di

import kotlinx.coroutines.CoroutineScope
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astrarating.AstraRating
import ru.astrainteractive.astrarating.api.rating.api.RatingDBApi
import ru.astrainteractive.astrarating.api.rating.usecase.InsertUserUseCase
import ru.astrainteractive.astrarating.command.rating.RatingCommandController
import ru.astrainteractive.astrarating.gui.ratings.RatingsGUI
import ru.astrainteractive.astrarating.model.EmpireConfig
import ru.astrainteractive.astrarating.plugin.PluginTranslation
import ru.astrainteractive.klibs.kdi.Factory
import ru.astrainteractive.klibs.kdi.Module

interface CommandsModule : Module {
    val plugin: AstraRating
    val dispatchers: BukkitDispatchers
    val scope: CoroutineScope
    val translation: PluginTranslation
    val config: EmpireConfig
    val dbApi: RatingDBApi
    val insertUseCase: InsertUserUseCase
    val ratingCommandController: RatingCommandController
    fun ratingsGUIFactory(player: Player): Factory<RatingsGUI>
}
