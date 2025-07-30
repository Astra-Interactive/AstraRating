package ru.astrainteractive.astrarating.feature.ratings.player.presentation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import ru.astrainteractive.astrarating.data.exposed.dto.RatingType
import ru.astrainteractive.astrarating.data.exposed.dto.UserRatingDTO
import ru.astrainteractive.astrarating.data.exposed.model.UserRatingsSort
import java.util.UUID

interface RatingPlayerComponent : CoroutineScope {
    val model: StateFlow<Model>
    fun onSortClicked(isRightClick: Boolean = false)
    fun onDeleteClicked(item: UserRatingDTO)
    data class Model(
        val playerName: String,
        val playerUUID: UUID,
        val allRatings: List<UserRatingDTO> = emptyList(),
        val sort: UserRatingsSort = UserRatingsSort.Rating(true),
        val isLoading: Boolean = false
    ) {
        val userRatings: List<UserRatingDTO>
            get() = allRatings.filter { it.userCreatedReport != null }

        val killCounts: Int
            get() = allRatings.count { it.ratingType == RatingType.PLAYER_KILL }
    }
}
