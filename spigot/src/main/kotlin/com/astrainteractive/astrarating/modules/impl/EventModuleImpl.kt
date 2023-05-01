package com.astrainteractive.astrarating.modules.impl

import com.astrainteractive.astrarating.domain.api.RatingDBApi
import com.astrainteractive.astrarating.events.di.EventModule
import com.astrainteractive.astrarating.modules.RootModule
import com.astrainteractive.astrarating.plugin.EmpireConfig
import com.astrainteractive.astrarating.plugin.PluginTranslation
import kotlinx.coroutines.CoroutineScope
import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.astralibs.Dependency
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.events.EventListener
import ru.astrainteractive.astralibs.getValue

object EventModuleImpl : EventModule {
    private val rootModule: RootModule by RootModuleImpl

    override val configDependency: Dependency<EmpireConfig> = rootModule.config
    override val apiDependency: Dependency<RatingDBApi> = rootModule.dbApi
    override val translationDependency: Dependency<PluginTranslation> = rootModule.translation
    override val scope: Dependency<CoroutineScope> = rootModule.scope
    override val eventListener: Dependency<EventListener> = rootModule.eventListener
    override val plugin: Dependency<JavaPlugin> = rootModule.plugin
    override val dispatchers: Dependency<BukkitDispatchers> = rootModule.dispatchers
}
