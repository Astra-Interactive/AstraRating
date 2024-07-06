package ru.astrainteractive.astrarating.api.rating.di

import kotlinx.coroutines.CoroutineScope
import org.jetbrains.exposed.sql.Database
import ru.astrainteractive.astrarating.api.rating.api.CachedApi
import ru.astrainteractive.astrarating.api.rating.api.RatingDBApi
import ru.astrainteractive.astrarating.api.rating.api.impl.CachedApiImpl
import ru.astrainteractive.astrarating.api.rating.api.impl.RatingDBApiImpl
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.getValue

interface ApiRatingModule {
    val ratingDBApi: RatingDBApi
    val cachedApi: CachedApi

    class Default(
        database: Database,
        coroutineScope: CoroutineScope,
        isDebugProvider: Provider<Boolean>
    ) : ApiRatingModule {

        override val ratingDBApi: RatingDBApi by Provider {
            RatingDBApiImpl(
                database = database,
                isDebugProvider = isDebugProvider
            )
        }
        override val cachedApi: CachedApi by Provider {
            CachedApiImpl(
                databaseApi = ratingDBApi,
                scope = coroutineScope
            )
        }
    }
}
