package ru.astrainteractive.astrarating.gui.api.rating.players.util

import ru.astrainteractive.astrarating.data.dao.rating.model.RatedUser

internal interface RatingPlayersCachedRepository {
    suspend fun fetchUsersTotalRating(): List<RatedUser>

    fun clear()
}
