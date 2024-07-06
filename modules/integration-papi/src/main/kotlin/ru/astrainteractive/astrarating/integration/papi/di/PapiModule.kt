package ru.astrainteractive.astrarating.integration.papi.di

import kotlinx.coroutines.CoroutineScope
import org.bukkit.Bukkit
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astrarating.api.rating.api.CachedApi
import ru.astrainteractive.astrarating.core.EmpireConfig
import ru.astrainteractive.astrarating.integration.papi.RatingPAPILifecycle
import ru.astrainteractive.klibs.kdi.Dependency
import ru.astrainteractive.klibs.kdi.getValue

interface PapiModule {
    val lifecycle: Lifecycle?

    class Default(
        cachedApi: CachedApi,
        config: Dependency<EmpireConfig>,
        scope: CoroutineScope
    ) : PapiModule {
        private val papiDependencies = object : PapiDependencies {
            override val cachedApi: CachedApi = cachedApi
            override val config by config
            override val scope: CoroutineScope = scope
        }
        override val lifecycle: Lifecycle? by lazy {
            if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                RatingPAPILifecycle(papiDependencies)
            } else {
                null
            }
        }
    }
}
