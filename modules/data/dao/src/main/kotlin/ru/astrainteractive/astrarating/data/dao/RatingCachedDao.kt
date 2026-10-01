package ru.astrainteractive.astrarating.data.dao

import java.util.UUID

/**
 * Api for cached rating data
 */
interface RatingCachedDao {
    /**
     * @param uuid - uuid of the player
     * @return rating of player or 0 if it's not cached
     */
    fun getPlayerRating(uuid: UUID): Int

    suspend fun loadPlayerRating(uuid: UUID)

    fun clear()
}
