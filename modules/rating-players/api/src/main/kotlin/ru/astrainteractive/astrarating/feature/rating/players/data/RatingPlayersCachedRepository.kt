package ru.astrainteractive.astrarating.feature.rating.players.data

import ru.astrainteractive.astrarating.dto.RatedUserDTO

internal interface RatingPlayersCachedRepository {
    suspend fun fetchUsersTotalRating(): List<RatedUserDTO>

    fun clear()
}
