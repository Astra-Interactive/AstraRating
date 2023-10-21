package ru.astrainteractive.astrarating.feature.playerrating

import kotlinx.coroutines.flow.StateFlow
import ru.astrainteractive.astrarating.dto.UserAndRating
import ru.astrainteractive.astrarating.model.PlayerModel
import ru.astrainteractive.astrarating.model.UserRatingsSort

interface PlayerRatingsComponent {
    val model: StateFlow<Model>
    fun onSortClicked()
    fun onDeleteClicked(item: UserAndRating)
    fun close()
    data class Model(
        val playerModel: PlayerModel,
        val userRatings: List<UserAndRating> = emptyList(),
        val sort: UserRatingsSort = UserRatingsSort.values().first(),
        val isLoading: Boolean = false
    )
}
