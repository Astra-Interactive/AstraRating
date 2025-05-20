package ru.astrainteractive.astrarating.feature.allrating.model

import ru.astrainteractive.astrarating.dto.RatedUserDTO
import ru.astrainteractive.astrarating.model.UsersRatingsSort

data class AllRatingsState(
    val userRatings: List<RatedUserDTO> = emptyList(),
    val sort: UsersRatingsSort = UsersRatingsSort.ASC,
    val isLoading: Boolean = false
)
