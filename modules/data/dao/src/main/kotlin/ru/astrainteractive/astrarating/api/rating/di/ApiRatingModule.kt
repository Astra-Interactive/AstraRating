package ru.astrainteractive.astrarating.api.rating.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import org.jetbrains.exposed.sql.Database
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astrarating.api.rating.api.CachedApi
import ru.astrainteractive.astrarating.api.rating.api.RatingDao
import ru.astrainteractive.astrarating.api.rating.api.impl.CachedApiImpl
import ru.astrainteractive.astrarating.api.rating.api.impl.RatingDaoImpl

class ApiRatingModule(
    databaseFlow: Flow<Database>,
    coroutineScope: CoroutineScope,
    isDebugProvider: () -> Boolean
) {

    val ratingDao: RatingDao by lazy {
        RatingDaoImpl(
            databaseFlow = databaseFlow,
            isDebugProvider = isDebugProvider
        )
    }

    val cachedApi: CachedApi by lazy {
        CachedApiImpl(
            databaseApi = ratingDao,
            scope = coroutineScope
        )
    }

    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onDisable = {
            cachedApi.clear()
        }
    )
}
