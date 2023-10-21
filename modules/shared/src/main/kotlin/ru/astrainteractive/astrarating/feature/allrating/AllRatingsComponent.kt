package ru.astrainteractive.astrarating.feature.allrating

import kotlinx.coroutines.flow.StateFlow
import ru.astrainteractive.astrarating.dto.UserAndRating
import ru.astrainteractive.astrarating.model.UsersRatingsSort

interface AllRatingsComponent {
    val model: StateFlow<Model>
    fun onSortClicked()
    fun close()

    data class Model(
        val userRatings: List<UserAndRating> = emptyList(),
        val sort: UsersRatingsSort = UsersRatingsSort.ASC,
        val isLoading: Boolean = false
    )
}
