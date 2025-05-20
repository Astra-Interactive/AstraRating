package ru.astrainteractive.astrarating.feature.allrating.data

import ru.astrainteractive.astrarating.dto.RatedUserDTO

internal interface AllRatingsCachedRepository {
    suspend fun fetchUsersTotalRating(): List<RatedUserDTO>

    fun clear()
}
