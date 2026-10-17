package ru.astrainteractive.astrarating.data.dao.rating.api

import java.util.UUID

interface RatingCache {
    /**
     * @return rating of player or 0 if it's not cached
     */
    fun getPlayerRating(uuid: UUID): Int

    suspend fun loadPlayerRating(uuid: UUID)

    fun clear()
}
