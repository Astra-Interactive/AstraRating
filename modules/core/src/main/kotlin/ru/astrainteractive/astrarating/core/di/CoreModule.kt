package ru.astrainteractive.astrarating.core.di

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import ru.astrainteractive.astralibs.async.CoroutineFeature
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
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
    val lifecycle: Lifecycle
    val config: StateFlowKrate<EmpireConfig>
    val translation: StateFlowKrate<PluginTranslation>
    val scope: CoroutineFeature
    val dispatchers: KotlinDispatchers

    class Default(
        dataFolder: File,
        override val dispatchers: KotlinDispatchers,
    ) : CoreModule {

        override val translation = DefaultStateFlowMutableKrate(
            factory = ::PluginTranslation,
            loader = {
                val file = dataFolder.resolve("translations.yml")
                val serializer = YamlStringFormat()
                serializer.parse<PluginTranslation>(file)
                    .onFailure(Throwable::printStackTrace)
                    .getOrElse { PluginTranslation() }
                    .also { serializer.writeIntoFile(it, file) }
            }
        )
        override val config = DefaultStateFlowMutableKrate(
            factory = ::EmpireConfig,
            loader = {
                val file = dataFolder.resolve("config.yml")
                val serializer = YamlStringFormat()
                serializer.parse<EmpireConfig>(file)
                    .onFailure(Throwable::printStackTrace)
                    .getOrElse { EmpireConfig() }
                    .also { serializer.writeIntoFile(it, file) }
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
