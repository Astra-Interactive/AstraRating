@file:Suppress("FunctionNaming")

package ru.astrainteractive.astrarating.data.exposed.rating

import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import ru.astrainteractive.astrarating.data.dao.rating.fake.FakeRatingRepository
import ru.astrainteractive.astrarating.data.dao.rating.model.RatedUser
import ru.astrainteractive.astrarating.data.dao.rating.model.User
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant

internal class Cache4kRatedUsersCacheTest {
    private val ratingRepository = FakeRatingRepository()
    private val tester = RatedUser(
        user = User(
            id = 1L,
            minecraftUUID = "f3d28cb0-7225-3cb1-baeb-2dadd2be89ae",
            minecraftName = "tester",
            lastUpdated = Instant.fromEpochMilliseconds(0L)
        ),
        ratingTotal = 3,
        ratingCounts = 2L
    )
    private val changedTester = tester.copy(ratingTotal = 7, ratingCounts = 3L)

    private fun TestScope.createCache(): Cache4kRatedUsersCache {
        return Cache4kRatedUsersCache(
            ratingRepository = ratingRepository,
            coroutineScope = backgroundScope
        )
    }

    @Test
    fun GIVEN_rated_users_stored_WHEN_totals_are_fetched_THEN_returns_them() {
        runTest {
            ratingRepository.setRatedUsers(listOf(tester))
            val cache = createCache()

            val ratedUsers = cache.fetchUsersTotalRating()

            assertEquals(listOf(tester), ratedUsers)
        }
    }

    @Test
    fun GIVEN_totals_fetched_recently_WHEN_storage_changes_THEN_cached_totals_are_returned() {
        runTest {
            ratingRepository.setRatedUsers(listOf(tester))
            val cache = createCache()
            cache.fetchUsersTotalRating()
            ratingRepository.setRatedUsers(listOf(changedTester))

            val ratedUsers = cache.fetchUsersTotalRating()

            assertEquals(listOf(tester), ratedUsers)
        }
    }

    @Test
    fun GIVEN_cache_cleared_WHEN_totals_are_fetched_THEN_returns_stored_totals() {
        runTest {
            ratingRepository.setRatedUsers(listOf(tester))
            val cache = createCache()
            cache.fetchUsersTotalRating()
            ratingRepository.setRatedUsers(listOf(changedTester))

            cache.clear()

            assertEquals(listOf(changedTester), cache.fetchUsersTotalRating())
        }
    }

    @Test
    fun GIVEN_database_unavailable_WHEN_totals_are_fetched_THEN_returns_no_users() {
        runTest {
            ratingRepository.setRatedUsers(listOf(tester))
            ratingRepository.isAvailable = false
            val cache = createCache()

            val ratedUsers = cache.fetchUsersTotalRating()

            assertEquals(emptyList(), ratedUsers)
        }
    }

    @Test
    fun GIVEN_database_was_unavailable_WHEN_totals_are_fetched_again_THEN_they_are_loaded() {
        runTest {
            ratingRepository.setRatedUsers(listOf(tester))
            ratingRepository.isAvailable = false
            val cache = createCache()
            cache.fetchUsersTotalRating()
            ratingRepository.isAvailable = true

            val ratedUsers = cache.fetchUsersTotalRating()

            assertEquals(listOf(tester), ratedUsers)
        }
    }
}
