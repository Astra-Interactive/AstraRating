package com.astrainteractive.astrarating.modules

import com.astrainteractive.astrarating.utils.EmpireConfig
import com.astrainteractive.astrarating.utils.Files
import ru.astrainteractive.astralibs.AstraYamlParser
import ru.astrainteractive.astralibs.Logger
import ru.astrainteractive.astralibs.di.IReloadable

object ConfigProvider: IReloadable<EmpireConfig>() {
    override fun initializer(): EmpireConfig {
        val config = try {
            AstraYamlParser.fileConfigurationToClass<EmpireConfig>(Files.configFile.fileConfiguration) ?: EmpireConfig()
        } catch (e: java.lang.Exception) {
            Logger.error("Could not load config.yml check for errors. Loaded default configs", "Config")
            EmpireConfig()
        }
        return config
    }
}