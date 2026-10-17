package ru.astrainteractive.astrarating.feature.rating.players.data

import ru.astrainteractive.astrarating.data.dao.rating.model.RatedUser

internal interface RatingPlayersCachedRepository {
    suspend fun fetchUsersTotalRating(): List<RatedUser>

    fun clear()
}
