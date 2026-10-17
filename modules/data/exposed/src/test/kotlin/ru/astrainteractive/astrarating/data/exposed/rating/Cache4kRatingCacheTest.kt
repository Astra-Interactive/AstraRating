@file:Suppress("FunctionNaming")

package ru.astrainteractive.astrarating.data.exposed.rating

import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import ru.astrainteractive.astrarating.data.dao.rating.fake.FakeRatingRepository
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals

internal class Cache4kRatingCacheTest {
    private val tester = UUID.fromString("f3d28cb0-7225-3cb1-baeb-2dadd2be89ae")
    private val ratingRepository = FakeRatingRepository()

    private fun TestScope.createDao(): Cache4kRatingCache {
        return Cache4kRatingCache(
            ratingRepository = ratingRepository,
            scope = backgroundScope
        )
    }

    @Test
    fun GIVEN_rating_not_loaded_WHEN_rating_is_read_THEN_returns_zero_until_it_is_loaded() = runTest {
        ratingRepository.setTotalRating(tester, 3)
        val dao = createDao()

        val notLoadedRating = dao.getPlayerRating(tester)
        runCurrent()

        assertEquals(0, notLoadedRating)
        assertEquals(3, dao.getPlayerRating(tester))
    }

    @Test
    fun GIVEN_rating_loaded_WHEN_rating_is_read_THEN_returns_total() = runTest {
        ratingRepository.setTotalRating(tester, 3)
        val dao = createDao()

        dao.loadPlayerRating(tester)

        assertEquals(3, dao.getPlayerRating(tester))
    }

    @Test
    fun GIVEN_rating_changed_WHEN_rating_is_loaded_again_THEN_returns_new_total() = runTest {
        ratingRepository.setTotalRating(tester, 3)
        val dao = createDao()
        dao.loadPlayerRating(tester)
        ratingRepository.setTotalRating(tester, 7)

        dao.loadPlayerRating(tester)

        assertEquals(7, dao.getPlayerRating(tester))
    }

    @Test
    fun GIVEN_database_was_unavailable_WHEN_rating_is_read_again_THEN_it_is_loaded() = runTest {
        ratingRepository.setTotalRating(tester, 3)
        ratingRepository.isAvailable = false
        val dao = createDao()
        dao.loadPlayerRating(tester)
        ratingRepository.isAvailable = true

        dao.getPlayerRating(tester)
        runCurrent()

        assertEquals(3, dao.getPlayerRating(tester))
    }
}
