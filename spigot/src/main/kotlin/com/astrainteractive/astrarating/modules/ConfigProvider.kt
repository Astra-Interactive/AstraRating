package com.astrainteractive.astrarating.modules

import com.astrainteractive.astrarating.utils.EmpireConfig
import com.astrainteractive.astrarating.utils.Files
import ru.astrainteractive.astralibs.EmpireSerializer
import ru.astrainteractive.astralibs.Logger
import ru.astrainteractive.astralibs.di.Reloadable
import ru.astrainteractive.astralibs.utils.toClass

object ConfigProvider : Reloadable<EmpireConfig>() {
    override fun initializer(): EmpireConfig {
        return EmpireSerializer.toClass<EmpireConfig>(Files.configFile) ?: let {
            Logger.error("Could not load config.yml check for errors. Loaded default configs", "Config")
            EmpireConfig()
        }
    }
}