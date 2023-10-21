package ru.astrainteractive.astrarating.feature.allrating.data

import ru.astrainteractive.astrarating.dto.RatedUserDTO

interface AllRatingsRepository {
    suspend fun fetchUsersTotalRating(): List<RatedUserDTO>
}
