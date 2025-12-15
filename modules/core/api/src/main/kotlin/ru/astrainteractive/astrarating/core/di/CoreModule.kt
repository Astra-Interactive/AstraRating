package ru.astrainteractive.astrarating.core.di

import com.charleskorn.kaml.PolymorphismStyle
import com.charleskorn.kaml.Yaml
import kotlinx.coroutines.cancel
import kotlinx.serialization.StringFormat
import ru.astrainteractive.astralibs.coroutines.withTimings
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.util.YamlStringFormat
import ru.astrainteractive.astralibs.util.parseOrWriteIntoDefault
import ru.astrainteractive.astrarating.core.settings.AstraRatingConfig
import ru.astrainteractive.astrarating.core.settings.AstraRatingTranslation
import ru.astrainteractive.klibs.kstorage.api.impl.DefaultMutableKrate
import ru.astrainteractive.klibs.kstorage.util.asStateFlowMutableKrate
import ru.astrainteractive.klibs.mikro.core.coroutines.CoroutineFeature
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import ru.astrainteractive.klibs.mikro.core.logging.JUtiltLogger
import ru.astrainteractive.klibs.mikro.core.logging.Logger
import java.io.File

class CoreModule(
    dataFolder: File,
    val dispatchers: KotlinDispatchers,
) : Logger by JUtiltLogger("AstraRating-CoreModule") {

    val yamlStringFormat: StringFormat by lazy {
        YamlStringFormat(
            configuration = Yaml.default.configuration.copy(
                encodeDefaults = true,
                strictMode = false,
                polymorphismStyle = PolymorphismStyle.Property
            ),
        )
    }
    val translationKrate = DefaultMutableKrate(
        factory = ::AstraRatingTranslation,
        loader = {
            yamlStringFormat.parseOrWriteIntoDefault(
                file = dataFolder.resolve("translations.yml"),
                default = ::AstraRatingTranslation,
                logger = this
            )
        }
    ).asStateFlowMutableKrate()

    val configKrate = DefaultMutableKrate(
        factory = ::AstraRatingConfig,
        loader = {
            yamlStringFormat.parseOrWriteIntoDefault(
                file = dataFolder.resolve("config.yml"),
                default = ::AstraRatingConfig,
                logger = this
            )
        }
    ).asStateFlowMutableKrate()
    val ioScope = CoroutineFeature.IO.withTimings()
    val mainScope = CoroutineFeature.Default(dispatchers.Main)
    val lifecycle: Lifecycle by lazy {
        Lifecycle.Lambda(
            onReload = {
                configKrate.getValue()
                translationKrate.getValue()
            },
            onDisable = {
                ioScope.cancel()
            }
        )
    }
}
