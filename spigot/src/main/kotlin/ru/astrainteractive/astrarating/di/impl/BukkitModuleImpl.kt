package ru.astrainteractive.astrarating.di.impl

import org.bstats.bukkit.Metrics
import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astralibs.async.DefaultBukkitDispatchers
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.astralibs.filemanager.DefaultFileConfigurationManager
import ru.astrainteractive.astralibs.menu.event.DefaultInventoryClickEvent
import ru.astrainteractive.astralibs.serialization.KyoriComponentSerializer
import ru.astrainteractive.astralibs.serialization.SerializerExt.parseOrDefault
import ru.astrainteractive.astralibs.serialization.YamlSerializer
import ru.astrainteractive.astrarating.AstraRating
import ru.astrainteractive.astrarating.core.EmpireConfig
import ru.astrainteractive.astrarating.core.PluginTranslation
import ru.astrainteractive.astrarating.di.BukkitModule
import ru.astrainteractive.astrarating.feature.changerating.data.BukkitPlatformBridge
import ru.astrainteractive.astrarating.feature.changerating.data.PlatformBridge
import ru.astrainteractive.klibs.kdi.Factory
import ru.astrainteractive.klibs.kdi.Lateinit
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.Reloadable
import ru.astrainteractive.klibs.kdi.Single
import ru.astrainteractive.klibs.kdi.getValue

class BukkitModuleImpl : BukkitModule {
    // Core
    override val plugin = Lateinit<AstraRating>()
    override val inventoryClickEvent: Single<DefaultInventoryClickEvent> = Single {
        DefaultInventoryClickEvent()
    }
    private val serializer by lazy {
        YamlSerializer()
    }

    override val kyoriComponentSerializer: Reloadable<KyoriComponentSerializer> = Reloadable {
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
        val configFileManager = DefaultFileConfigurationManager(plugin, "config.yml")
        serializer.parseOrDefault(configFileManager.configFile, ::EmpireConfig)
    }
    override val translation = Reloadable {
        val plugin by plugin
        val file = DefaultFileConfigurationManager(plugin, "translations.yml").configFile
        serializer.parseOrDefault(file, ::PluginTranslation)
    }

    override val platformBridge: Provider<PlatformBridge> = Provider {
        BukkitPlatformBridge(
            minTimeOnServer = { config.value.minTimeOnServer }
        )
    }
}
