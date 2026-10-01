@file:Suppress("FunctionNaming")

package ru.astrainteractive.astrarating.data.dao.impl

import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import ru.astrainteractive.astrarating.data.dao.fake.FakeRatingDao
import ru.astrainteractive.astrarating.data.exposed.dto.RatingType
import ru.astrainteractive.astrarating.data.exposed.dto.UserDTO
import ru.astrainteractive.astrarating.data.exposed.dto.UserRatingDTO
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals

internal class RatingCachedDaoImplTest {
    private val tester = UUID.fromString("f3d28cb0-7225-3cb1-baeb-2dadd2be89ae")
    private val ratingDao = FakeRatingDao()

    private fun rating(value: Int, type: RatingType): UserRatingDTO {
        return UserRatingDTO(
            id = 1L,
            userCreatedReport = null,
            reportedUser = UserDTO(
                id = 1L,
                minecraftUUID = tester.toString(),
                minecraftName = "Tester",
                lastUpdated = 0L
            ),
            rating = value,
            message = "",
            ratingType = type,
            time = 0L
        )
    }

    private fun TestScope.createDao(): RatingCachedDaoImpl {
        return RatingCachedDaoImpl(
            databaseApi = ratingDao,
            scope = backgroundScope
        )
    }

    @Test
    fun GIVEN_online_player_WHEN_rating_is_read_for_the_first_time_THEN_returns_sum_of_ratings() = runTest {
        ratingDao.setRatings(tester, listOf(rating(5, RatingType.USER_RATING), rating(-2, RatingType.PLAYER_KILL)))
        val dao = createDao()

        dao.markOnline(tester)
        runCurrent()

        assertEquals(3, dao.getPlayerRating("Tester", tester))
    }

    @Test
    fun GIVEN_online_player_missing_from_database_WHEN_rating_is_read_THEN_returns_zero() = runTest {
        val dao = createDao()

        dao.markOnline(tester)
        runCurrent()

        assertEquals(0, dao.getPlayerRating("Tester", tester))
    }

    @Test
    fun GIVEN_player_left_and_rating_changed_WHEN_player_joins_again_THEN_returns_new_rating() = runTest {
        ratingDao.setRatings(tester, listOf(rating(3, RatingType.USER_RATING)))
        val dao = createDao()
        dao.markOnline(tester)
        runCurrent()
        dao.markOffline(tester)
        ratingDao.setRatings(tester, listOf(rating(7, RatingType.USER_RATING)))

        dao.markOnline(tester)
        runCurrent()

        assertEquals(7, dao.getPlayerRating("Tester", tester))
    }

    @Test
    fun GIVEN_player_left_before_rating_loaded_WHEN_player_joins_again_THEN_returns_new_rating() = runTest {
        ratingDao.setRatings(tester, listOf(rating(3, RatingType.USER_RATING)))
        val dao = createDao()
        dao.markOnline(tester)
        dao.markOffline(tester)
        runCurrent()
        ratingDao.setRatings(tester, listOf(rating(7, RatingType.USER_RATING)))

        dao.markOnline(tester)
        runCurrent()

        assertEquals(7, dao.getPlayerRating("Tester", tester))
    }

    @Test
    fun GIVEN_offline_player_WHEN_rating_was_requested_before_THEN_returns_sum_of_ratings() = runTest {
        ratingDao.setRatings(tester, listOf(rating(4, RatingType.USER_RATING)))
        val dao = createDao()
        dao.getPlayerRating("Tester", tester)
        runCurrent()

        assertEquals(4, dao.getPlayerRating("Tester", tester))
    }
}
