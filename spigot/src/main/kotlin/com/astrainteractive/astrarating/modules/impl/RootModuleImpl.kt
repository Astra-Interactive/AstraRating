@file:OptIn(UnsafeApi::class)

package com.astrainteractive.astrarating.modules.impl

import com.astrainteractive.astrarating.AstraRating
import com.astrainteractive.astrarating.domain.api.CachedApi
import com.astrainteractive.astrarating.domain.api.RatingDBApi
import com.astrainteractive.astrarating.domain.api.impl.CachedApiImpl
import com.astrainteractive.astrarating.domain.api.impl.RatingDBApiImpl
import com.astrainteractive.astrarating.domain.usecases.InsertUserUseCase
import com.astrainteractive.astrarating.events.EventManager
import com.astrainteractive.astrarating.integrations.papi.RatingPAPIExpansion
import com.astrainteractive.astrarating.modules.DBFactory
import com.astrainteractive.astrarating.modules.RootModule
import com.astrainteractive.astrarating.plugin.EmpireConfig
import com.astrainteractive.astrarating.plugin.PluginTranslation
import org.bstats.bukkit.Metrics
import org.jetbrains.kotlin.tooling.core.UnsafeApi
import ru.astrainteractive.astralibs.Dependency
import ru.astrainteractive.astralibs.Factory
import ru.astrainteractive.astralibs.Lateinit
import ru.astrainteractive.astralibs.Reloadable
import ru.astrainteractive.astralibs.Single
import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.async.DefaultBukkitDispatchers
import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.configloader.ConfigLoader
import ru.astrainteractive.astralibs.events.EventListener
import ru.astrainteractive.astralibs.events.GlobalEventListener
import ru.astrainteractive.astralibs.filemanager.DefaultSpigotFileManager
import ru.astrainteractive.astralibs.getValue

object RootModuleImpl : RootModule {
    override val plugin = Lateinit<AstraRating>()
    override val bstats = Factory {
        val plugin by plugin
        Metrics(plugin, 15801)
    }
    override val configFileManager = Single {
        val plugin by plugin
        DefaultSpigotFileManager(plugin, "config.yml")
    }
    override val papiExpansion: Dependency<RatingPAPIExpansion> = Single {
        RatingPAPIExpansion(PapiModuleImpl)
    }

    override val scope: Dependency<AsyncComponent> = Single {
        PluginScope
    }
    override val eventListener: Dependency<EventListener> = Single {
        GlobalEventListener
    }
    override val dispatchers: Dependency<BukkitDispatchers> = Single {
        val plugin by plugin
        DefaultBukkitDispatchers(plugin)
    }
    override val config = Reloadable {
        ConfigLoader.toClassOrDefault(configFileManager.value.configFile, ::EmpireConfig)
    }
    override val translation = Reloadable {
        val plugin by plugin
        PluginTranslation(plugin)
    }
    override val database = Single {
        val plugin by plugin
        DBFactory(plugin, config).build()
    }

    override val dbApi = Single {
        RatingDBApiImpl(database.value) as RatingDBApi
    }
    override val cachedApi = Single {
        val dispatchers by dispatchers
        val scope by scope
        CachedApiImpl(dbApi.value, dispatchers.IO, scope) as CachedApi
    }
    override val eventManager = Factory {
        EventManager(EventModuleImpl)
    }
    override val insertUserUseCase = Single {
        InsertUserUseCase(dbApi.value) {
            null
        }
    }
}
