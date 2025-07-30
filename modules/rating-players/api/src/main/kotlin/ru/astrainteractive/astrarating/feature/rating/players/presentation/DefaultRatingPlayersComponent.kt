package ru.astrainteractive.astrarating.feature.rating.players.presentation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.astrainteractive.astralibs.async.CoroutineFeature
import ru.astrainteractive.astrarating.core.util.sortedBy
import ru.astrainteractive.astrarating.data.exposed.model.UsersRatingsSort
import ru.astrainteractive.astrarating.feature.rating.players.data.RatingPlayersCachedRepository
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import kotlin.math.absoluteValue

internal class DefaultRatingPlayersComponent(
    private val repository: RatingPlayersCachedRepository,
    dispatchers: KotlinDispatchers
) : RatingPlayersComponent, CoroutineFeature by CoroutineFeature.Default(dispatchers.Main) {
    override val model = MutableStateFlow(RatingPlayersComponent.Model())

    override fun onSortClicked(isRightClick: Boolean) {
        val sorts = listOf(
            UsersRatingsSort.TotalRating(true),
            UsersRatingsSort.TotalRating(false),
        )
        val offset = if (isRightClick) -1 else 1
        val i = sorts.indexOfFirst { sortType -> sortType == model.value.sort }

        val newSortType = if (i == -1) {
            sorts.first()
        } else if (i + offset == -1) {
            sorts[sorts.lastIndex]
        } else {
            sorts[(i + offset).absoluteValue % sorts.size]
        }

        val userRatings = model.value.userRatings
        val sortedUserRatings = userRatings.sortedBy(newSortType.isAsc) { it.ratingTotal }
        model.update {
            it.copy(
                userRatings = sortedUserRatings,
                sort = newSortType
            )
        }
    }

    init {
        launch(dispatchers.IO) {
            model.update { it.copy(isLoading = true) }
            val userRatings = repository.fetchUsersTotalRating()
            model.update { it.copy(userRatings = userRatings) }
            onSortClicked()
            model.update { it.copy(isLoading = false) }
        }
    }
}
