package com.astrainteractive.astrarating.modules

import com.astrainteractive.astrarating.AstraRating
import com.astrainteractive.astrarating.domain.api.CachedApi
import com.astrainteractive.astrarating.domain.api.RatingDBApi
import com.astrainteractive.astrarating.domain.usecases.InsertUserUseCase
import com.astrainteractive.astrarating.events.EventManager
import com.astrainteractive.astrarating.integrations.papi.RatingPAPIComponent
import com.astrainteractive.astrarating.plugin.EmpireConfig
import com.astrainteractive.astrarating.plugin.PluginTranslation
import org.bstats.bukkit.Metrics
import ru.astrainteractive.astralibs.Dependency
import ru.astrainteractive.astralibs.Factory
import ru.astrainteractive.astralibs.Module
import ru.astrainteractive.astralibs.Reloadable
import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.events.EventListener
import ru.astrainteractive.astralibs.filemanager.SpigotFileManager
import ru.astrainteractive.astralibs.orm.Database

interface RootModule : Module {
    val plugin: Dependency<AstraRating>
    val bstats: Factory<Metrics>
    val eventListener: Dependency<EventListener>
    val dispatchers: Dependency<BukkitDispatchers>
    val scope: Dependency<AsyncComponent>
    val papiExpansion: Dependency<RatingPAPIComponent?>
    val configFileManager: Dependency<SpigotFileManager>
    val config: Reloadable<EmpireConfig>
    val translation: Reloadable<PluginTranslation>
    val database: Dependency<Database>
    val dbApi: Dependency<RatingDBApi>
    val cachedApi: Dependency<CachedApi>
    val eventManager: Factory<EventManager>
    val insertUserUseCase: Dependency<InsertUserUseCase>
}
