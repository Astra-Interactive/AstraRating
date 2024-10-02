package ru.astrainteractive.astrarating.core.di

import com.charleskorn.kaml.PolymorphismStyle
import com.charleskorn.kaml.Yaml
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.serialization.StringFormat
import ru.astrainteractive.astralibs.async.CoroutineFeature
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.logging.JUtiltLogger
import ru.astrainteractive.astralibs.logging.Logger
import ru.astrainteractive.astralibs.serialization.StringFormatExt.parse
import ru.astrainteractive.astralibs.serialization.StringFormatExt.writeIntoFile
import ru.astrainteractive.astralibs.serialization.YamlStringFormat
import ru.astrainteractive.astrarating.core.EmpireConfig
import ru.astrainteractive.astrarating.core.PluginTranslation
import ru.astrainteractive.klibs.kstorage.api.flow.StateFlowKrate
import ru.astrainteractive.klibs.kstorage.api.impl.DefaultStateFlowMutableKrate
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import java.io.File

interface CoreModule {
    val yamlStringFormat: StringFormat
    val lifecycle: Lifecycle
    val config: StateFlowKrate<EmpireConfig>
    val translation: StateFlowKrate<PluginTranslation>
    val scope: CoroutineFeature
    val dispatchers: KotlinDispatchers

    class Default(
        dataFolder: File,
        override val dispatchers: KotlinDispatchers,
    ) : CoreModule, Logger by JUtiltLogger("AstraRating-CoreModule") {

        override val yamlStringFormat: StringFormat by lazy {
            YamlStringFormat(
                configuration = Yaml.default.configuration.copy(
                    encodeDefaults = true,
                    strictMode = false,
                    polymorphismStyle = PolymorphismStyle.Property
                ),
            )
        }
        override val translation = DefaultStateFlowMutableKrate(
            factory = ::PluginTranslation,
            loader = {
                val file = dataFolder.resolve("translations.yml")
                val defaultFile = dataFolder.resolve("translations.default.yml")
                yamlStringFormat.parse<PluginTranslation>(file)
                    .onFailure {
                        defaultFile.createNewFile()
                        yamlStringFormat.writeIntoFile(it, defaultFile)
                        error { "Could not read translations.yml! Loaded default. Error -> ${it.message}" }
                    }
                    .onSuccess {
                        yamlStringFormat.writeIntoFile(it, file)
                    }
                    .getOrElse { PluginTranslation() }
            }
        )
        override val config = DefaultStateFlowMutableKrate(
            factory = ::EmpireConfig,
            loader = {
                val file = dataFolder.resolve("config.yml")
                val defaultFile = dataFolder.resolve("config.default.yml")
                yamlStringFormat.parse<EmpireConfig>(file)
                    .onFailure {
                        defaultFile.createNewFile()
                        yamlStringFormat.writeIntoFile(it, defaultFile)
                        error { "Could not read config.yml! Loaded default. Error -> ${it.message}" }
                    }
                    .onSuccess {
                        yamlStringFormat.writeIntoFile(it, file)
                    }
                    .getOrElse { EmpireConfig() }
            }
        )
        override val scope = CoroutineFeature.Default(Dispatchers.IO)

        override val lifecycle: Lifecycle by lazy {
            Lifecycle.Lambda(
                onReload = {
                    config.loadAndGet()
                    translation.loadAndGet()
                },
                onDisable = {
                    scope.cancel()
                }
            )
        }
    }
}
