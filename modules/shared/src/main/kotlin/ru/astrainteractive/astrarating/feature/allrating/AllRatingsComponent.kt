package ru.astrainteractive.astrarating.feature.allrating

import kotlinx.coroutines.flow.StateFlow
import ru.astrainteractive.astrarating.dto.RatedUserDTO
import ru.astrainteractive.astrarating.model.UsersRatingsSort

interface AllRatingsComponent {
    val model: StateFlow<Model>
    fun onSortClicked()
    fun close()

    data class Model(
        val userRatings: List<RatedUserDTO> = emptyList(),
        val sort: UsersRatingsSort = UsersRatingsSort.ASC,
        val isLoading: Boolean = false
    )
}
