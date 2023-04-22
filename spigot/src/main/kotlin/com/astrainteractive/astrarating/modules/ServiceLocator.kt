package com.astrainteractive.astrarating.modules

import com.astrainteractive.astrarating.AstraRating
import com.astrainteractive.astrarating.domain.api.CachedApi
import com.astrainteractive.astrarating.domain.api.CachedApiImpl
import com.astrainteractive.astrarating.domain.api.RatingDBApi
import com.astrainteractive.astrarating.domain.api.RatingDBApiImpl
import com.astrainteractive.astrarating.domain.use_cases.InsertUserUseCase
import com.astrainteractive.astrarating.events.EventManager
import com.astrainteractive.astrarating.plugin.EmpireConfig
import com.astrainteractive.astrarating.plugin.Files
import com.astrainteractive.astrarating.plugin.PluginTranslation
import com.astrainteractive.astrarating.utils.getLinkedDiscordID
import kotlinx.coroutines.Dispatchers
import org.bstats.bukkit.Metrics
import ru.astrainteractive.astralibs.EmpireSerializer
import ru.astrainteractive.astralibs.Logger
import ru.astrainteractive.astralibs.async.BukkitAsync
import ru.astrainteractive.astralibs.di.module
import ru.astrainteractive.astralibs.di.reloadable
import ru.astrainteractive.astralibs.utils.toClass

object ServiceLocator {
    val bstats = module {
        Metrics(AstraRating.instance, 15801)
    }
    val config = reloadable {
        EmpireSerializer.toClass<EmpireConfig>(Files.configFile) ?: let {
            Logger.error("Could not load config.yml check for errors. Loaded default configs", "Config")
            EmpireConfig()
        }
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