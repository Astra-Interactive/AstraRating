package ru.astrainteractive.astrarating.feature.allrating.data

import ru.astrainteractive.astrarating.dto.RatedUserDTO

internal interface AllRatingsRepository {
    suspend fun fetchUsersTotalRating(): List<RatedUserDTO>
}
