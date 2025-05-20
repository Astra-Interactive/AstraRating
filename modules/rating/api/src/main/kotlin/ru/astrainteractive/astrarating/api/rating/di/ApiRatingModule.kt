package ru.astrainteractive.astrarating.api.rating.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import org.jetbrains.exposed.sql.Database
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astrarating.api.rating.api.CachedApi
import ru.astrainteractive.astrarating.api.rating.api.RatingDBApi
import ru.astrainteractive.astrarating.api.rating.api.impl.CachedApiImpl
import ru.astrainteractive.astrarating.api.rating.api.impl.RatingDBApiImpl

interface ApiRatingModule {
    val lifecycle: Lifecycle

    val ratingDBApi: RatingDBApi
    val cachedApi: CachedApi

    class Default(
        databaseFlow: Flow<Database>,
        coroutineScope: CoroutineScope,
        isDebugProvider: () -> Boolean
    ) : ApiRatingModule {

        override val ratingDBApi: RatingDBApi by lazy {
            RatingDBApiImpl(
                databaseFlow = databaseFlow,
                isDebugProvider = isDebugProvider
            )
        }

        override val cachedApi: CachedApi by lazy {
            CachedApiImpl(
                databaseApi = ratingDBApi,
                scope = coroutineScope
            )
        }

        override val lifecycle: Lifecycle = Lifecycle.Lambda(
            onDisable = {
                cachedApi.clear()
            }
        )
    }
}
