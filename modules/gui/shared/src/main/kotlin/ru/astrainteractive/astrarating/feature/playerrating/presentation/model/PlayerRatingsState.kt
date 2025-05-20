package ru.astrainteractive.astrarating.feature.playerrating.presentation.model

import ru.astrainteractive.astrarating.dto.RatingType
import ru.astrainteractive.astrarating.dto.UserRatingDTO
import ru.astrainteractive.astrarating.model.UserRatingsSort
import java.util.UUID

data class PlayerRatingsState(
    val playerName: String,
    val playerUUID: UUID,
    val allRatings: List<UserRatingDTO> = emptyList(),
    val sort: UserRatingsSort = UserRatingsSort.entries.first(),
    val isLoading: Boolean = false
) {
    val userRatings: List<UserRatingDTO>
        get() = allRatings.filter { it.userCreatedReport != null }

    val killCounts: Int
        get() = allRatings.count { it.ratingType == RatingType.PLAYER_KILL }
}