package ru.astrainteractive.astrarating.di.impl

import org.bstats.bukkit.Metrics
import org.jetbrains.kotlin.tooling.core.UnsafeApi
import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astralibs.async.DefaultBukkitDispatchers
import ru.astrainteractive.astralibs.configloader.ConfigLoader
import ru.astrainteractive.astralibs.event.GlobalEventListener
import ru.astrainteractive.astralibs.filemanager.DefaultSpigotFileManager
import ru.astrainteractive.astrarating.AstraRating
import ru.astrainteractive.astrarating.di.RootModule
import ru.astrainteractive.astrarating.di.ServicesModule
import ru.astrainteractive.astrarating.event.EventManager
import ru.astrainteractive.astrarating.model.EmpireConfig
import ru.astrainteractive.astrarating.plugin.PluginTranslation
import ru.astrainteractive.klibs.kdi.Factory
import ru.astrainteractive.klibs.kdi.Lateinit
import ru.astrainteractive.klibs.kdi.Reloadable
import ru.astrainteractive.klibs.kdi.Single
import ru.astrainteractive.klibs.kdi.getValue

class ServicesModuleImpl(rootModule: RootModule) : ServicesModule {
    // Core
    override val plugin = Lateinit<AstraRating>()

    // Services
    override val bstats = Factory {
        val plugin by plugin
        Metrics(plugin, 15801)
    }
    override val configFileManager = Single {
        val plugin by plugin
        DefaultSpigotFileManager(plugin, "config.yml")
    }

    override val scope = Single {
        AsyncComponent.Default()
    }

    @OptIn(UnsafeApi::class)
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
    override val eventManager = Factory {
        val eventModule = EventModuleImpl(rootModule)
        EventManager(eventModule)
    }
}
