package ru.astrainteractive.astrarating.di

import org.bstats.bukkit.Metrics
import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.astralibs.filemanager.SpigotFileManager
import ru.astrainteractive.astralibs.orm.Database
import ru.astrainteractive.astrarating.AstraRating
import ru.astrainteractive.astrarating.api.rating.api.CachedApi
import ru.astrainteractive.astrarating.api.rating.api.RatingDBApi
import ru.astrainteractive.astrarating.api.rating.usecase.InsertUserUseCase
import ru.astrainteractive.astrarating.command.di.CommandsModule
import ru.astrainteractive.astrarating.event.EventManager
import ru.astrainteractive.astrarating.event.di.EventModule
import ru.astrainteractive.astrarating.gui.di.GuiModule
import ru.astrainteractive.astrarating.integration.papi.RatingPAPIComponent
import ru.astrainteractive.astrarating.integration.papi.di.PapiModule
import ru.astrainteractive.astrarating.model.EmpireConfig
import ru.astrainteractive.astrarating.plugin.PluginTranslation
import ru.astrainteractive.klibs.kdi.Factory
import ru.astrainteractive.klibs.kdi.Lateinit
import ru.astrainteractive.klibs.kdi.Module
import ru.astrainteractive.klibs.kdi.Reloadable
import ru.astrainteractive.klibs.kdi.Single

interface RootModule : Module {
    // Core
    val plugin: Lateinit<AstraRating>

    // Modules
    val commandsModule: CommandsModule
    val papiModule: PapiModule
    val eventModule: EventModule
    val guiModule: GuiModule

    // Etc
    val bstats: Factory<Metrics>
    val eventListener: Single<EventListener>
    val dispatchers: Single<BukkitDispatchers>
    val scope: Single<AsyncComponent>
    val papiExpansion: Single<RatingPAPIComponent?>
    val configFileManager: Single<SpigotFileManager>
    val config: Reloadable<EmpireConfig>
    val translation: Reloadable<PluginTranslation>
    val database: Single<Database>
    val dbApi: Single<RatingDBApi>
    val cachedApi: Single<CachedApi>
    val eventManager: Factory<EventManager>
    val insertUserUseCase: Single<InsertUserUseCase>
}
