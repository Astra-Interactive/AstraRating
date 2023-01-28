package com.astrainteractive.astrarating.modules

import com.astrainteractive.astrarating.utils.EmpireConfig
import com.astrainteractive.astrarating.utils.Files
import com.charleskorn.kaml.Yaml
import kotlinx.serialization.encodeToString
import ru.astrainteractive.astralibs.EmpireSerializer
import ru.astrainteractive.astralibs.Logger
import ru.astrainteractive.astralibs.di.IReloadable
import ru.astrainteractive.astralibs.file_manager.FileManager
import ru.astrainteractive.astralibs.utils.toClass
import java.io.File

object ConfigProvider: IReloadable<EmpireConfig>() {
    override fun initializer(): EmpireConfig {
        return EmpireSerializer.toClass<EmpireConfig>(Files.configFile)?: let {
            Logger.error("Could not load config.yml check for errors. Loaded default configs", "Config")
            EmpireConfig()
        }
    }
}