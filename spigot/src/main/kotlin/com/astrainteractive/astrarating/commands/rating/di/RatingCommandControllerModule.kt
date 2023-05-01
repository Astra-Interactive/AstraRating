package com.astrainteractive.astrarating.commands.rating.di

import com.astrainteractive.astrarating.domain.api.RatingDBApi
import com.astrainteractive.astrarating.domain.usecases.InsertUserUseCase
import com.astrainteractive.astrarating.gui.ratings.RatingsGUI
import com.astrainteractive.astrarating.plugin.EmpireConfig
import com.astrainteractive.astrarating.plugin.PluginTranslation
import kotlinx.coroutines.CoroutineScope
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.Dependency
import ru.astrainteractive.astralibs.Factory
import ru.astrainteractive.astralibs.async.BukkitDispatchers

interface RatingCommandControllerModule {
    val dispatchers: Dependency<BukkitDispatchers>
    val scope: Dependency<CoroutineScope>
    val translation: Dependency<PluginTranslation>
    val config: Dependency<EmpireConfig>
    val dbApi: Dependency<RatingDBApi>
    val insertUseCase: Dependency<InsertUserUseCase>
    fun ratingsGUIFactory(player: Player): Factory<RatingsGUI>
}
