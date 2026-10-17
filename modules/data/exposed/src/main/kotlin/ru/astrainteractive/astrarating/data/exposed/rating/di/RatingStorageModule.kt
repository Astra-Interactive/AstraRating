package ru.astrainteractive.astrarating.data.exposed.rating.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import org.jetbrains.exposed.v1.jdbc.Database
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astrarating.data.dao.rating.api.RatingCache
import ru.astrainteractive.astrarating.data.dao.rating.api.RatingRepository
import ru.astrainteractive.astrarating.data.exposed.rating.Cache4kRatingCache
import ru.astrainteractive.astrarating.data.exposed.rating.CacheRefreshingRatingRepository
import ru.astrainteractive.astrarating.data.exposed.rating.ExposedRatingRepository
import kotlin.time.Clock

class RatingStorageModule(
    databaseFlow: Flow<Database>,
    coroutineScope: CoroutineScope,
    isDebugProvider: () -> Boolean
) {

    private val exposedRatingRepository: RatingRepository by lazy {
        ExposedRatingRepository(
            databaseFlow = databaseFlow,
            clock = Clock.System,
            isDebugProvider = isDebugProvider
        )
    }

    val ratingCache: RatingCache by lazy {
        Cache4kRatingCache(
            databaseApi = exposedRatingRepository,
            scope = coroutineScope
        )
    }

    val ratingRepository: RatingRepository by lazy {
        CacheRefreshingRatingRepository(
            ratingRepository = exposedRatingRepository,
            ratingCache = ratingCache
        )
    }

    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onDisable = {
            ratingCache.clear()
        }
    )
}
