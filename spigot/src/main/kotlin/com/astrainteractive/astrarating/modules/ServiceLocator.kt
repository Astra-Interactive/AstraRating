package com.astrainteractive.astrarating.modules

import com.astrainteractive.astrarating.AstraRating
import com.astrainteractive.astrarating.domain.api.CachedApi
import com.astrainteractive.astrarating.domain.api.CachedApiImpl
import com.astrainteractive.astrarating.domain.api.RatingDBApi
import com.astrainteractive.astrarating.domain.api.RatingDBApiImpl
import com.astrainteractive.astrarating.domain.use_cases.InsertUserUseCase
import com.astrainteractive.astrarating.events.EventManager
import com.astrainteractive.astrarating.plugin.EmpireConfig
import com.astrainteractive.astrarating.plugin.PluginTranslation
import com.astrainteractive.astrarating.utils.getLinkedDiscordID
import kotlinx.coroutines.Dispatchers
import org.bstats.bukkit.Metrics
import ru.astrainteractive.astralibs.async.BukkitAsync
import ru.astrainteractive.astralibs.configloader.ConfigLoader
import ru.astrainteractive.astralibs.di.module
import ru.astrainteractive.astralibs.di.reloadable
import ru.astrainteractive.astralibs.filemanager.SpigotFileManager

object ServiceLocator {
    val bstats = module {
        Metrics(AstraRating.instance, 15801)
    }
    val configFileManager = module {
        SpigotFileManager("config.yml")
    }
    val config = reloadable {
        ConfigLoader.toClassOrDefault(configFileManager.value.configFile,EmpireConfig())
    }
    val translation = reloadable {
        PluginTranslation()
    }
    val database = module {
        DBFactory(config).value
    }

    val dbApi = module {
        RatingDBApiImpl(database.value) as RatingDBApi
    }
    val cachedApi = module {
        CachedApiImpl(dbApi.value, Dispatchers.BukkitAsync) as CachedApi
    }
    val eventManager = module {
        EventManager(
            configDependency = config,
            apiDependency = dbApi,
            translationDependency = translation
        )
    }
    val insertUserUseCase = module {
        InsertUserUseCase(dbApi.value) {
            getLinkedDiscordID(it)
        }
    }

}