package ru.astrainteractive.astrarating.integration.papi.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.StringFormat
import org.bukkit.Bukkit
import ru.astrainteractive.astralibs.expansion.PlaceholderExpansionFacade
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.logging.JUtiltLogger
import ru.astrainteractive.astralibs.logging.Logger
import ru.astrainteractive.astralibs.serialization.StringFormatExt.parse
import ru.astrainteractive.astralibs.serialization.StringFormatExt.writeIntoFile
import ru.astrainteractive.astrarating.api.rating.api.CachedApi
import ru.astrainteractive.astrarating.integration.papi.di.factory.PapiFactory
import ru.astrainteractive.astrarating.integration.papi.model.PapiConfig
import ru.astrainteractive.klibs.kstorage.api.impl.DefaultStateFlowMutableKrate
import java.io.File

interface PapiModule {
    val lifecycle: Lifecycle

    class Default(
        cachedApi: CachedApi,
        scope: CoroutineScope,
        dataFolder: File,
        yamlStringFormat: StringFormat
    ) : PapiModule, Logger by JUtiltLogger("AstraRating-PapiModule") {
        private val isPapiEnabled: Boolean
            get() = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")

        private val papiConfiguration = DefaultStateFlowMutableKrate(
            factory = ::PapiConfig,
            loader = {
                val file = dataFolder.resolve("papi.yml")
                val defaultFile = dataFolder.resolve("papi.default.yml")
                yamlStringFormat.parse<PapiConfig>(file)
                    .onFailure {
                        defaultFile.createNewFile()
                        yamlStringFormat.writeIntoFile(PapiConfig(), defaultFile)
                        error { "Could not read papi.yml! Loaded default. Error -> ${it.message}" }
                    }
                    .onSuccess {
                        yamlStringFormat.writeIntoFile(it, file)
                    }
                    .getOrElse { PapiConfig() }
            }
        )

        private val placeholderFacade: PlaceholderExpansionFacade by lazy {
            PapiFactory(
                dependencies = PapiDependencies.Default(
                    cachedApi = cachedApi,
                    scope = scope,
                    getPapiConfiguration = { papiConfiguration.cachedValue }
                )
            ).create()
        }
        override val lifecycle: Lifecycle = Lifecycle.Lambda(
            onEnable = {
                if (isPapiEnabled) {
                    placeholderFacade.register()
                }
            },
            onReload = {
                if (isPapiEnabled) {
                    if (placeholderFacade.isRegistered()) placeholderFacade.unregister()
                    placeholderFacade.register()
                }
            },
            onDisable = {
                if (isPapiEnabled) {
                    if (placeholderFacade.isRegistered()) placeholderFacade.unregister()
                }
            }
        )
    }
}
