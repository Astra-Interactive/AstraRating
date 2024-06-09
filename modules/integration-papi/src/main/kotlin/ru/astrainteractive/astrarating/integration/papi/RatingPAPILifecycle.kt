package ru.astrainteractive.astrarating.integration.papi

import org.bukkit.Bukkit
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astrarating.integration.papi.di.PapiDependencies

internal class RatingPAPILifecycle(dependencies: PapiDependencies) : Lifecycle {
    private val expansion = RatingPAPIExpansion(dependencies)
    private val isPapiExists: Boolean
        get() = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null

    override fun onEnable() {
        if (!isPapiExists) {
            Bukkit.getLogger().warning("Could not find PlaceholderAPI. Placeholders disabled")
            return
        }
        Bukkit.getLogger().warning("Found PlaceholderAPI. Placeholders are now enabled")
        if (expansion.isRegistered) {
            expansion.unregister()
        }
        expansion.register()
    }

    override fun onReload() {
        if (!isPapiExists) return
        onDisable()
        onEnable()
    }

    override fun onDisable() {
        if (!isPapiExists) return
        expansion.unregister()
    }
}
