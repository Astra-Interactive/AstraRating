package ru.astrainteractive.astrarating.di.impl

import org.bstats.bukkit.Metrics
import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astralibs.async.DefaultBukkitDispatchers
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.astralibs.filemanager.DefaultSpigotFileManager
import ru.astrainteractive.astralibs.menu.event.DefaultInventoryClickEvent
import ru.astrainteractive.astralibs.permission.BukkitPermissionManager
import ru.astrainteractive.astralibs.permission.PermissionManager
import ru.astrainteractive.astralibs.serialization.KyoriComponentSerializer
import ru.astrainteractive.astralibs.serialization.YamlSerializer
import ru.astrainteractive.astrarating.AstraRating
import ru.astrainteractive.astrarating.di.RootModule
import ru.astrainteractive.astrarating.di.ServicesModule
import ru.astrainteractive.astrarating.event.EventManager
import ru.astrainteractive.astrarating.model.EmpireConfig
import ru.astrainteractive.astrarating.model.PluginTranslation
import ru.astrainteractive.klibs.kdi.Factory
import ru.astrainteractive.klibs.kdi.Lateinit
import ru.astrainteractive.klibs.kdi.Reloadable
import ru.astrainteractive.klibs.kdi.Single
import ru.astrainteractive.klibs.kdi.getValue

class ServicesModuleImpl(rootModule: RootModule) : ServicesModule {
    // Core
    override val plugin = Lateinit<AstraRating>()
    override val inventoryClickEvent: Single<DefaultInventoryClickEvent> = Single {
        DefaultInventoryClickEvent()
    }
    override val componentSerializer: Single<KyoriComponentSerializer> = Single {
        KyoriComponentSerializer.Legacy
    }

    // Services
    override val bstats = Factory {
        val plugin by plugin
        Metrics(plugin, 15801)
    }

    override val scope = Single {
        AsyncComponent.Default()
    }

    override val eventListener: Single<EventListener> = Single {
        object : EventListener {}
    }
    override val dispatchers = Single {
        val plugin by plugin
        DefaultBukkitDispatchers(plugin)
    }
    override val config = Reloadable {
        val plugin by plugin
        val configFileManager = DefaultSpigotFileManager(plugin, "config.yml")
        YamlSerializer().toClassOrDefault(configFileManager.configFile, ::EmpireConfig)
    }
    override val translation = Reloadable {
        val plugin by plugin
        val file = DefaultSpigotFileManager(plugin, "translations.yml").configFile
        YamlSerializer().toClassOrDefault(file, ::PluginTranslation)
    }
    override val eventManager = Factory {
        val eventModule = EventModuleImpl(rootModule)
        EventManager(eventModule)
    }
    override val permissionManager: Single<PermissionManager> = Single {
        BukkitPermissionManager()
    }
}
