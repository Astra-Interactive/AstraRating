package ru.astrainteractive.astrarating.gui.api.rating.players.component

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import ru.astrainteractive.astrarating.data.dao.rating.model.RatedUser
import ru.astrainteractive.astrarating.gui.api.rating.players.model.UsersRatingsSort

interface RatingPlayersComponent : CoroutineScope {
    val model: StateFlow<Model>

    fun onSortClicked(isRightClick: Boolean = false)

    data class Model(
        val userRatings: List<RatedUser>,
        val sort: UsersRatingsSort,
        val isLoading: Boolean
    )
}
