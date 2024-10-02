package ru.astrainteractive.astrarating

import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astrarating.di.impl.RootModuleImpl

/**
 * Initial class for your plugin
 */
class AstraRating : LifecyclePlugin() {
    private val rootModule = RootModuleImpl(this)
    private val lifecycles: List<Lifecycle>
        get() = listOfNotNull(
            rootModule.coreModule.lifecycle,
            rootModule.bukkitModule.lifecycle,
            rootModule.dbRatingModule.lifecycle,
            rootModule.commandsModule.lifecycle,
            rootModule.eventModule.lifecycle,
            rootModule.papiModule?.lifecycle
        )

    /**
     * This method called when server starts or PlugMan load plugin.
     */
    override fun onEnable() {
        lifecycles.forEach(Lifecycle::onEnable)
    }

    /**
     * This method called when server is shutting down or when PlugMan disable plugin.
     */
    override fun onDisable() {
        lifecycles.forEach(Lifecycle::onDisable)
    }

    /**
     * As it says, function for plugin reload
     */
    fun reloadPlugin() {
        lifecycles.forEach(Lifecycle::onReload)
    }
}
