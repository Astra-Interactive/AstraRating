package ru.astrainteractive.astrarating.core.di

import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.serialization.StringFormatExt.parse
import ru.astrainteractive.astralibs.serialization.StringFormatExt.writeIntoFile
import ru.astrainteractive.astralibs.serialization.YamlStringFormat
import ru.astrainteractive.astrarating.core.EmpireConfig
import ru.astrainteractive.astrarating.core.PluginTranslation
import ru.astrainteractive.klibs.kdi.Dependency
import ru.astrainteractive.klibs.kdi.Reloadable
import ru.astrainteractive.klibs.kdi.Single
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import java.io.File

interface CoreModule {
    val lifecycle: Lifecycle
    val config: Reloadable<EmpireConfig>
    val translation: Dependency<PluginTranslation>
    val scope: Dependency<AsyncComponent>
    val dispatchers: KotlinDispatchers

    class Default(
        dataFolder: File,
        override val dispatchers: KotlinDispatchers,
    ) : CoreModule {

        override val translation: Reloadable<PluginTranslation> = Reloadable {
            val file = dataFolder.resolve("translations.yml")
            val serializer = YamlStringFormat()
            serializer.parse<PluginTranslation>(file)
                .onFailure(Throwable::printStackTrace)
                .getOrElse { PluginTranslation() }
                .also { serializer.writeIntoFile(it, file) }
        }

        override val config: Reloadable<EmpireConfig> = Reloadable {
            val file = dataFolder.resolve("config.yml")
            val serializer = YamlStringFormat()
            serializer.parse<EmpireConfig>(file)
                .onFailure(Throwable::printStackTrace)
                .getOrElse { EmpireConfig() }
                .also { serializer.writeIntoFile(it, file) }
        }
        override val scope: Dependency<AsyncComponent> = Single {
            AsyncComponent.Default()
        }

        override val lifecycle: Lifecycle by lazy {
            Lifecycle.Lambda(
                onReload = {
                    config.reload()
                    translation.reload()
                },
                onDisable = {
                    scope.value.close()
                }
            )
        }
    }
}
