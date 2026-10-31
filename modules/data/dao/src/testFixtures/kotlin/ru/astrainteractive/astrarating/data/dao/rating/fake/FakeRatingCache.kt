package ru.astrainteractive.astrarating.data.exposed.rating.fake

import ru.astrainteractive.astrarating.data.dao.rating.api.RatingCache
import java.util.UUID

internal class FakeRatingCache : RatingCache {
    val loadedPlayers = mutableListOf<UUID>()

    override fun resolvePlayerRating(uuid: UUID): Int = 0

    override suspend fun loadPlayerRating(uuid: UUID) {
        loadedPlayers += uuid
    }

    override fun clear() = Unit
}
