package ru.astrainteractive.astrarating.gui.api.rating.player.component

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import ru.astrainteractive.astrarating.data.dao.rating.model.RatingType
import ru.astrainteractive.astrarating.data.dao.rating.model.UserRating
import ru.astrainteractive.astrarating.gui.api.rating.player.model.UserRatingsSort
import java.util.UUID

interface RatingPlayerComponent : CoroutineScope {
    val model: StateFlow<Model>
    fun onSortClicked(isRightClick: Boolean = false)
    fun onDeleteClicked(item: UserRating)
    data class Model(
        val playerName: String,
        val playerUUID: UUID,
        val allRatings: List<UserRating> = emptyList(),
        val sort: UserRatingsSort = UserRatingsSort.Rating(true),
        val isLoading: Boolean = false
    ) {
        val userRatings: List<UserRating>
            get() = allRatings.filter { it.userCreatedReport != null }

        val killCounts: Int
            get() = allRatings.count { it.ratingType == RatingType.PLAYER_KILL }
    }
}
