package ru.astrainteractive.astrarating.feature.rating.players.data

import ru.astrainteractive.astrarating.data.exposed.dto.RatedUserDTO

internal interface RatingPlayersCachedRepository {
    suspend fun fetchUsersTotalRating(): List<RatedUserDTO>

    fun clear()
}
