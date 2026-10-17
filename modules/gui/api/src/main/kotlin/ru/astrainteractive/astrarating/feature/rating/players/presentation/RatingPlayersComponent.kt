package ru.astrainteractive.astrarating.feature.rating.players.presentation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import ru.astrainteractive.astrarating.data.dao.rating.model.RatedUser
import ru.astrainteractive.astrarating.feature.rating.players.model.UsersRatingsSort

interface RatingPlayersComponent : CoroutineScope {
    val model: StateFlow<Model>
    fun onSortClicked(isRightClick: Boolean = false)

    data class Model(
        val userRatings: List<RatedUser> = emptyList(),
        val sort: UsersRatingsSort = UsersRatingsSort.TotalRating(true),
        val isLoading: Boolean = false
    )
}
