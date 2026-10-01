package ru.astrainteractive.astrarating.data.dao.fake

import ru.astrainteractive.astrarating.data.dao.RatingCachedDao
import java.util.UUID

class FakeRatingCachedDao(ratings: Map<UUID, Int>) : RatingCachedDao {
    private val cachedRatings = ratings.toMutableMap()

    override fun getPlayerRating(name: String, uuid: UUID): Int = cachedRatings[uuid] ?: 0

    override fun clear() {
        cachedRatings.clear()
    }
}
