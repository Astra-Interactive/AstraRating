package com.astrainteractive.astrarating.commands.di

import com.astrainteractive.astrarating.AstraRating
import com.astrainteractive.astrarating.commands.rating.RatingCommandController
import com.astrainteractive.astrarating.domain.api.RatingDBApi
import com.astrainteractive.astrarating.domain.usecases.InsertUserUseCase
import com.astrainteractive.astrarating.gui.ratings.RatingsGUI
import com.astrainteractive.astrarating.plugin.EmpireConfig
import com.astrainteractive.astrarating.plugin.PluginTranslation
import kotlinx.coroutines.CoroutineScope
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.async.BukkitDispatchers
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
