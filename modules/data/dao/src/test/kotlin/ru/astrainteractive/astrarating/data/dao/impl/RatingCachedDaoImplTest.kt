@file:Suppress("FunctionNaming")

package ru.astrainteractive.astrarating.data.dao.impl

import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import ru.astrainteractive.astrarating.data.dao.fake.FakeRatingDao
import ru.astrainteractive.astrarating.data.exposed.dto.RatedUserDTO
import ru.astrainteractive.astrarating.data.exposed.dto.UserDTO
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals

internal class RatingCachedDaoImplTest {
    private val tester = UUID.fromString("f3d28cb0-7225-3cb1-baeb-2dadd2be89ae")
    private val reporter = UUID.fromString("00000000-0000-0000-0000-000000000001")

    private fun ratedUser(uuid: UUID, name: String, ratingTotal: Int): RatedUserDTO {
        return RatedUserDTO(
            userDTO = UserDTO(
                id = 1L,
                minecraftUUID = uuid.toString(),
                minecraftName = name,
                lastUpdated = 0L
            ),
            ratingTotal = ratingTotal,
            ratingCounts = 1L
        )
    }

    private fun TestScope.createLoadedDao(usersTotalRating: Result<List<RatedUserDTO>>): RatingCachedDaoImpl {
        val dao = RatingCachedDaoImpl(
            databaseApi = FakeRatingDao(usersTotalRating),
            scope = backgroundScope
        )
        runCurrent()
        return dao
    }

    @Test
    fun GIVEN_rated_player_WHEN_rating_is_read_for_the_first_time_THEN_returns_total_rating() = runTest {
        val dao = createLoadedDao(Result.success(listOf(ratedUser(tester, "Tester", 3))))

        assertEquals(3, dao.getPlayerRating("Tester", tester))
    }

    @Test
    fun GIVEN_several_rated_players_WHEN_ratings_are_read_THEN_each_player_gets_own_total() = runTest {
        val dao = createLoadedDao(
            Result.success(
                listOf(
                    ratedUser(tester, "Tester", 3),
                    ratedUser(reporter, "Reporter", -4)
                )
            )
        )

        assertEquals(3, dao.getPlayerRating("Tester", tester))
        assertEquals(-4, dao.getPlayerRating("Reporter", reporter))
    }

    @Test
    fun GIVEN_empty_database_WHEN_rating_is_read_THEN_returns_zero() = runTest {
        val dao = createLoadedDao(Result.success(emptyList()))

        assertEquals(0, dao.getPlayerRating("Tester", tester))
    }

    @Test
    fun GIVEN_database_failure_WHEN_rating_is_read_THEN_returns_zero() = runTest {
        val dao = createLoadedDao(Result.failure(IllegalStateException("Database is unavailable")))

        assertEquals(0, dao.getPlayerRating("Tester", tester))
    }
}
