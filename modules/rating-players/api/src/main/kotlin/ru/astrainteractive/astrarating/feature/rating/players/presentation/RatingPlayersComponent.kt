package ru.astrainteractive.astrarating.feature.rating.players.presentation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import ru.astrainteractive.astrarating.data.exposed.dto.RatedUserDTO
import ru.astrainteractive.astrarating.data.exposed.model.UsersRatingsSort

interface RatingPlayersComponent : CoroutineScope {
    val model: StateFlow<Model>
    fun onSortClicked(isRightClick: Boolean = false)

    data class Model(
        val userRatings: List<RatedUserDTO> = emptyList(),
        val sort: UsersRatingsSort = UsersRatingsSort.Players(true),
        val isLoading: Boolean = false
    )
}
