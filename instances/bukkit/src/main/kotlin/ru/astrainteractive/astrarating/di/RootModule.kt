package ru.astrainteractive.astrarating.di

import ru.astrainteractive.astralibs.async.DefaultBukkitDispatchers
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.lifecycle.LifecyclePlugin
import ru.astrainteractive.astrarating.api.rating.di.ApiRatingModule
import ru.astrainteractive.astrarating.command.di.CommandsModule
import ru.astrainteractive.astrarating.core.di.CoreModule
import ru.astrainteractive.astrarating.db.rating.di.DBRatingModule
import ru.astrainteractive.astrarating.event.di.EventModule
import ru.astrainteractive.astrarating.feature.changerating.di.RatingChangeModule
import ru.astrainteractive.astrarating.feature.rating.players.di.RatingPlayersModule
import ru.astrainteractive.astrarating.feature.ratings.player.di.RatingPlayerModule
import ru.astrainteractive.astrarating.gui.di.GuiModule
import ru.astrainteractive.astrarating.integration.papi.di.PapiModule

class RootModule(plugin: LifecyclePlugin) {
    private val bukkitModule: BukkitModule by lazy {
        BukkitModule.Default(plugin)
    }

    private val coreModule: CoreModule by lazy {
        CoreModule.Default(
            dataFolder = bukkitModule.plugin.dataFolder,
            dispatchers = DefaultBukkitDispatchers(bukkitModule.plugin)
        )
    }

    private val dbRatingModule: DBRatingModule by lazy {
        DBRatingModule.Default(
            stringFormat = coreModule.yamlStringFormat,
            dataFolder = bukkitModule.plugin.dataFolder,
        )
    }

    private val apiRatingModule: ApiRatingModule by lazy {
        ApiRatingModule.Default(
            databaseFlow = dbRatingModule.databaseFlow,
            coroutineScope = coreModule.scope,
            isDebugProvider = { coreModule.config.cachedValue.debug }
        )
    }

    private val papiModule: PapiModule by lazy {
        PapiModule.Default(
            cachedApi = apiRatingModule.cachedApi,
            scope = coreModule.scope,
            dataFolder = bukkitModule.plugin.dataFolder,
            yamlStringFormat = coreModule.yamlStringFormat
        )
    }

    private val ratingChangeModule: RatingChangeModule by lazy {
        RatingChangeModule(
            dispatchers = coreModule.dispatchers,
            empireConfigKrate = coreModule.config,
            dbApi = apiRatingModule.ratingDBApi
        )
    }

    private val ratingPlayerModule: RatingPlayerModule by lazy {
        RatingPlayerModule(
            apiRatingModule = apiRatingModule,
            dispatchers = coreModule.dispatchers,
        )
    }

    private val ratingPlayersModule: RatingPlayersModule by lazy {
        RatingPlayersModule(
            apiRatingModule = apiRatingModule,
            dispatchers = coreModule.dispatchers,
            coroutineScope = coreModule.scope
        )
    }

    private val guiModule: GuiModule by lazy {
        GuiModule.Default(
            coreModule = coreModule,
            apiRatingModule = apiRatingModule,
            translationContext = bukkitModule.kyoriComponentSerializer.cachedValue,
            ratingPlayerModule = ratingPlayerModule,
            changeRatingModule = ratingChangeModule,
            ratingPlayersModule = ratingPlayersModule
        )
    }

    private val eventModule: EventModule by lazy {
        EventModule.Default(
            coreModule = coreModule,
            apiRatingModule = apiRatingModule,
            bukkitModule = bukkitModule
        )
    }

    private val commandsModule: CommandsModule by lazy {
        CommandsModule.Default(
            bukkitModule = bukkitModule,
            coreModule = coreModule,
            guiModule = guiModule,
            ratingChangeModule = ratingChangeModule
        )
    }
    private val lifecycles: List<Lifecycle>
        get() = listOfNotNull(
            coreModule.lifecycle,
            bukkitModule.lifecycle,
            dbRatingModule.lifecycle,
            apiRatingModule.lifecycle,
            ratingPlayersModule.lifecycle,
            commandsModule.lifecycle,
            eventModule.lifecycle,
            papiModule.lifecycle,
        )

    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onEnable = { lifecycles.forEach(Lifecycle::onEnable) },
        onReload = { lifecycles.forEach(Lifecycle::onReload) },
        onDisable = { lifecycles.forEach(Lifecycle::onDisable) },
    )
}
