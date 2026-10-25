package ru.astrainteractive.astrarating.data.exposed.rating.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import org.jetbrains.exposed.v1.jdbc.Database
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.server.bridge.PlatformServer
import ru.astrainteractive.astrarating.data.dao.rating.api.RatedUsersCache
import ru.astrainteractive.astrarating.data.dao.rating.api.RatingCache
import ru.astrainteractive.astrarating.data.dao.rating.api.RatingRepository
import ru.astrainteractive.astrarating.data.exposed.rating.Cache4kRatedUsersCache
import ru.astrainteractive.astrarating.data.exposed.rating.Cache4kRatingCache
import ru.astrainteractive.astrarating.data.exposed.rating.CacheRefreshingRatingRepository
import ru.astrainteractive.astrarating.data.exposed.rating.ExposedRatingRepository
import ru.astrainteractive.astrarating.data.exposed.rating.warmup.RatingCacheWarmUp
import kotlin.time.Clock

class RatingStorageModule(
    databaseFlow: Flow<Database>,
    coroutineScope: CoroutineScope,
    platformServer: PlatformServer,
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
            ratingRepository = exposedRatingRepository,
            scope = coroutineScope
        )
    }

    val ratedUsersCache: RatedUsersCache by lazy {
        Cache4kRatedUsersCache(
            ratingRepository = exposedRatingRepository,
            coroutineScope = coroutineScope
        )
    }

    val ratingRepository: RatingRepository by lazy {
        CacheRefreshingRatingRepository(
            ratingRepository = exposedRatingRepository,
            ratingCache = ratingCache
        )
    }

    private val ratingCacheWarmUp by lazy {
        RatingCacheWarmUp(
            platformServer = platformServer,
            ratingCache = ratingCache,
            ioScope = coroutineScope
        )
    }

    @Suppress("TrivialLambdaReference")
    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onEnable = {
            ratingCacheWarmUp.onEnable()
        },
        onDisable = {
            ratingCache.clear()
            ratedUsersCache.clear()
        }
    )
}
