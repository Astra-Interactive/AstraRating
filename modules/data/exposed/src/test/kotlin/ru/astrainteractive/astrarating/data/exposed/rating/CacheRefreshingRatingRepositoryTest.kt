@file:Suppress("FunctionNaming")

package ru.astrainteractive.astrarating.data.exposed.rating

import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import ru.astrainteractive.astrarating.data.dao.rating.fake.FakeRatingRepository
import ru.astrainteractive.astrarating.data.dao.rating.model.RatingType
import ru.astrainteractive.astrarating.data.dao.rating.model.User
import ru.astrainteractive.astrarating.data.dao.rating.model.UserRating
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant

internal class CacheRefreshingRatingRepositoryTest {
    private val tester = User(
        id = 1L,
        minecraftUUID = "f3d28cb0-7225-3cb1-baeb-2dadd2be89ae",
        minecraftName = "Tester",
        lastUpdated = Instant.fromEpochMilliseconds(0L)
    )
    private val testerUuid = UUID.fromString(tester.minecraftUUID)
    private val exposedRatingRepository = FakeRatingRepository()

    private fun TestScope.createCachedDao(): Cache4kRatingCache {
        return Cache4kRatingCache(
            ratingRepository = exposedRatingRepository,
            scope = backgroundScope
        )
    }

    @Test
    fun GIVEN_loaded_rating_WHEN_rating_is_inserted_THEN_cached_rating_includes_it() {
        runTest {
            exposedRatingRepository.setTotalRating(testerUuid, 3)
            val ratingCache = createCachedDao()
            val ratingRepository = CacheRefreshingRatingRepository(
                ratingRepository = exposedRatingRepository,
                ratingCache = ratingCache
            )
            ratingCache.loadPlayerRating(testerUuid)

            ratingRepository.insertUserRating(
                reporter = null,
                reported = tester,
                message = "kill",
                type = RatingType.PLAYER_KILL,
                ratingValue = -1
            )

            assertEquals(2, ratingCache.resolvePlayerRating(testerUuid))
        }
    }

    @Test
    fun GIVEN_loaded_rating_WHEN_rating_is_deleted_THEN_cached_rating_excludes_it() {
        runTest {
            exposedRatingRepository.setTotalRating(testerUuid, 3)
            val ratingCache = createCachedDao()
            val ratingRepository = CacheRefreshingRatingRepository(
                ratingRepository = exposedRatingRepository,
                ratingCache = ratingCache
            )
            ratingCache.loadPlayerRating(testerUuid)

            ratingRepository.deleteUserRating(
                UserRating(
                    id = 1L,
                    userCreatedReport = null,
                    reportedUser = tester,
                    rating = 1,
                    message = "like",
                    ratingType = RatingType.USER_RATING,
                    time = Instant.fromEpochMilliseconds(0L)
                )
            )

            assertEquals(2, ratingCache.resolvePlayerRating(testerUuid))
        }
    }
}
