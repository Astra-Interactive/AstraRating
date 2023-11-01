package ru.astrainteractive.astrarating.feature.playerrating.presentation

import kotlinx.coroutines.flow.StateFlow
import ru.astrainteractive.astrarating.dto.UserRatingDTO
import ru.astrainteractive.astrarating.model.PlayerModel
import ru.astrainteractive.astrarating.model.UserRatingsSort

interface PlayerRatingsComponent {
    val model: StateFlow<Model>
    fun onSortClicked()
    fun onDeleteClicked(item: UserRatingDTO)
    fun close()
    data class Model(
        val playerModel: PlayerModel,
        val userRatings: List<UserRatingDTO> = emptyList(),
        val sort: UserRatingsSort = UserRatingsSort.values().first(),
        val isLoading: Boolean = false
    )
}
