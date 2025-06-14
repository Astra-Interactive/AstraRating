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
import ru.astrainteractive.astralibs.serialization.StringFormatExt.parseOrWriteIntoDefault
import ru.astrainteractive.astralibs.serialization.YamlStringFormat
import ru.astrainteractive.astrarating.core.EmpireConfig
import ru.astrainteractive.astrarating.core.PluginTranslation
import ru.astrainteractive.klibs.kstorage.api.StateFlowKrate
import ru.astrainteractive.klibs.kstorage.api.impl.DefaultMutableKrate
import ru.astrainteractive.klibs.kstorage.util.asStateFlowMutableKrate
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import java.io.File

interface CoreModule {
    val lifecycle: Lifecycle

    val yamlStringFormat: StringFormat
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
        override val translation = DefaultMutableKrate(
            factory = ::PluginTranslation,
            loader = {
                yamlStringFormat.parseOrWriteIntoDefault(
                    file = dataFolder.resolve("translations.yml"),
                    default = ::PluginTranslation,
                    logger = this
                )
            }
        ).asStateFlowMutableKrate()

        override val config = DefaultMutableKrate(
            factory = ::EmpireConfig,
            loader = {
                yamlStringFormat.parseOrWriteIntoDefault(
                    file = dataFolder.resolve("config.yml"),
                    default = ::EmpireConfig,
                    logger = this
                )
            }
        ).asStateFlowMutableKrate()
        override val scope = CoroutineFeature.Default(Dispatchers.IO)

        override val lifecycle: Lifecycle by lazy {
            Lifecycle.Lambda(
                onReload = {
                    config.getValue()
                    translation.getValue()
                },
                onDisable = {
                    scope.cancel()
                }
            )
        }
    }
}
