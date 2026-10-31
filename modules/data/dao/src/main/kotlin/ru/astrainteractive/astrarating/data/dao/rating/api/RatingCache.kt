package ru.astrainteractive.astrarating.data.dao.rating.api

import java.util.UUID

interface RatingCache {
    fun resolvePlayerRating(uuid: UUID): Int

    suspend fun loadPlayerRating(uuid: UUID)

    fun clear()
}
