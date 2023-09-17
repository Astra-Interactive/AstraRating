@file:OptIn(UnsafeApi::class)

package com.astrainteractive.astrarating.modules.impl

import com.astrainteractive.astrarating.AstraRating
import com.astrainteractive.astrarating.domain.api.CachedApi
import com.astrainteractive.astrarating.domain.api.RatingDBApi
import com.astrainteractive.astrarating.domain.api.impl.CachedApiImpl
import com.astrainteractive.astrarating.domain.api.impl.RatingDBApiImpl
import com.astrainteractive.astrarating.domain.usecases.InsertUserUseCase
import com.astrainteractive.astrarating.events.EventManager
import com.astrainteractive.astrarating.integrations.papi.RatingPAPIComponent
import com.astrainteractive.astrarating.modules.DBFactory
import com.astrainteractive.astrarating.modules.RootModule
import com.astrainteractive.astrarating.plugin.EmpireConfig
import com.astrainteractive.astrarating.plugin.PluginTranslation
import org.bstats.bukkit.Metrics
import org.bukkit.Bukkit
import org.jetbrains.kotlin.tooling.core.UnsafeApi
import ru.astrainteractive.astralibs.async.DefaultBukkitDispatchers
import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.configloader.ConfigLoader
import ru.astrainteractive.astralibs.event.GlobalEventListener
import ru.astrainteractive.astralibs.filemanager.DefaultSpigotFileManager
import ru.astrainteractive.klibs.kdi.Factory
import ru.astrainteractive.klibs.kdi.Lateinit
import ru.astrainteractive.klibs.kdi.Reloadable
import ru.astrainteractive.klibs.kdi.Single
import ru.astrainteractive.klibs.kdi.getValue

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
    override val papiExpansion = Single {
        runCatching {
            if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                RatingPAPIComponent(PapiModuleImpl)
            } else {
                error("No PlaceholderAPI ")
            }
        }.onFailure {
            Bukkit.getLogger().severe("[AstraRating] You don't have PAPI installed. Placeholders will not be avaliable")
        }.getOrNull()
    }

    override val scope = Single {
        PluginScope
    }
    override val eventListener = Single {
        GlobalEventListener
    }
    override val dispatchers = Single {
        val plugin by plugin
        DefaultBukkitDispatchers(plugin)
    }
    override val config = Reloadable {
        ConfigLoader().toClassOrDefault(configFileManager.value.configFile, ::EmpireConfig)
    }
    override val translation = Reloadable {
        val plugin by plugin
        PluginTranslation(plugin)
    }
    override val database = Single {
        val plugin by plugin
        DBFactory(plugin, config).create()
    }

    override val dbApi = Single {
        RatingDBApiImpl(database.value, plugin.value.dataFolder) as RatingDBApi
    }
    override val cachedApi = Single {
        val scope by scope
        CachedApiImpl(dbApi.value, scope) as CachedApi
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
