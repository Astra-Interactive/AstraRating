@file:Suppress("FunctionNaming")

package ru.astrainteractive.astrarating.gui.api.rating.player.util

import ru.astrainteractive.astrarating.data.dao.rating.model.RatingType
import ru.astrainteractive.astrarating.data.dao.rating.model.User
import ru.astrainteractive.astrarating.data.dao.rating.model.UserRating
import ru.astrainteractive.astrarating.gui.api.rating.player.model.UserRatingsSort
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant

class ListExtTest {
    private val reported = User(
        id = 1L,
        minecraftUUID = "reported",
        minecraftName = "Reported",
        lastUpdated = Instant.fromEpochMilliseconds(0L)
    )
    private val ratings = listOf(
        rating(
            id = 1L,
            reporterName = "Bob",
            value = 1,
            time = Instant.fromEpochMilliseconds(30L)
        ),
        rating(
            id = 2L,
            reporterName = "Alice",
            value = -1,
            time = Instant.fromEpochMilliseconds(10L)
        ),
        rating(
            id = 3L,
            reporterName = "Carol",
            value = 3,
            time = Instant.fromEpochMilliseconds(20L)
        )
    )

    private fun rating(id: Long, reporterName: String, value: Int, time: Instant): UserRating {
        return UserRating(
            id = id,
            userCreatedReport = User(
                id = id + 1,
                minecraftUUID = reporterName,
                minecraftName = reporterName,
                lastUpdated = Instant.fromEpochMilliseconds(0L)
            ),
            reportedUser = reported,
            rating = value,
            message = "message",
            ratingType = RatingType.USER_RATING,
            time = time
        )
    }

    private fun List<UserRating>.ids(): List<Long> = map(UserRating::id)

    @Test
    fun GIVEN_ratings_WHEN_sorted_by_rating_ascending_THEN_lowest_value_comes_first() {
        assertEquals(
            listOf(
                2L,
                1L,
                3L
            ),
            ratings.sortedBy(UserRatingsSort.Rating(isAsc = true)).ids()
        )
    }

    @Test
    fun GIVEN_ratings_WHEN_sorted_by_player_descending_THEN_names_go_backwards() {
        assertEquals(
            listOf(
                3L,
                1L,
                2L
            ),
            ratings.sortedBy(UserRatingsSort.Player(isAsc = false)).ids()
        )
    }

    @Test
    fun GIVEN_ratings_WHEN_sorted_by_date_ascending_THEN_oldest_comes_first() {
        assertEquals(
            listOf(
                2L,
                3L,
                1L
            ),
            ratings.sortedBy(UserRatingsSort.Date(isAsc = true)).ids()
        )
    }
}
