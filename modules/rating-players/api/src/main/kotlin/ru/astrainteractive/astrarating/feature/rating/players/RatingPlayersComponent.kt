package ru.astrainteractive.astrarating.feature.rating.players

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import ru.astrainteractive.astrarating.dto.RatedUserDTO
import ru.astrainteractive.astrarating.model.UsersRatingsSort

interface RatingPlayersComponent : CoroutineScope {
    val model: StateFlow<Model>
    fun onSortClicked()

    data class Model(
        val userRatings: List<RatedUserDTO> = emptyList(),
        val sort: UsersRatingsSort = UsersRatingsSort.ASC,
        val isLoading: Boolean = false
    )
}
