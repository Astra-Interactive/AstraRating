@file:Suppress("FunctionNaming")

package ru.astrainteractive.astrarating.data.dao.impl

import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import ru.astrainteractive.astrarating.data.dao.fake.FakeRatingDao
import ru.astrainteractive.astrarating.data.exposed.dto.RatingType
import ru.astrainteractive.astrarating.data.exposed.dto.UserDTO
import ru.astrainteractive.astrarating.data.exposed.dto.UserRatingDTO
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals

internal class CacheRefreshingRatingDaoTest {
    private val tester = UserDTO(
        id = 1L,
        minecraftUUID = "f3d28cb0-7225-3cb1-baeb-2dadd2be89ae",
        minecraftName = "Tester",
        lastUpdated = 0L
    )
    private val testerUuid = UUID.fromString(tester.minecraftUUID)
    private val databaseRatingDao = FakeRatingDao()

    private fun TestScope.createCachedDao(): RatingCachedDaoImpl {
        return RatingCachedDaoImpl(
            databaseApi = databaseRatingDao,
            scope = backgroundScope
        )
    }

    @Test
    fun GIVEN_loaded_rating_WHEN_rating_is_inserted_THEN_cached_rating_includes_it() = runTest {
        databaseRatingDao.setTotalRating(testerUuid, 3)
        val ratingCachedDao = createCachedDao()
        val ratingDao = CacheRefreshingRatingDao(ratingDao = databaseRatingDao, ratingCachedDao = ratingCachedDao)
        ratingCachedDao.loadPlayerRating(testerUuid)

        ratingDao.insertUserRating(
            reporter = null,
            reported = tester,
            message = "kill",
            type = RatingType.PLAYER_KILL,
            ratingValue = -1
        )

        assertEquals(2, ratingCachedDao.getPlayerRating(testerUuid))
    }

    @Test
    fun GIVEN_loaded_rating_WHEN_rating_is_deleted_THEN_cached_rating_excludes_it() = runTest {
        databaseRatingDao.setTotalRating(testerUuid, 3)
        val ratingCachedDao = createCachedDao()
        val ratingDao = CacheRefreshingRatingDao(ratingDao = databaseRatingDao, ratingCachedDao = ratingCachedDao)
        ratingCachedDao.loadPlayerRating(testerUuid)

        ratingDao.deleteUserRating(
            UserRatingDTO(
                id = 1L,
                userCreatedReport = null,
                reportedUser = tester,
                rating = 1,
                message = "like",
                ratingType = RatingType.USER_RATING,
                time = 0L
            )
        )

        assertEquals(2, ratingCachedDao.getPlayerRating(testerUuid))
    }
}
