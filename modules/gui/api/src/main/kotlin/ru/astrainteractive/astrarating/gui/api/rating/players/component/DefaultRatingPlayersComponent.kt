package ru.astrainteractive.astrarating.gui.api.rating.players.component

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.astrainteractive.astralibs.coroutines.withTimings
import ru.astrainteractive.astrarating.gui.api.rating.players.model.UsersRatingsSort
import ru.astrainteractive.astrarating.gui.api.rating.players.util.RatedUsersCache
import ru.astrainteractive.astrarating.gui.api.util.nextAfter
import ru.astrainteractive.astrarating.gui.api.util.sortedBy
import ru.astrainteractive.klibs.mikro.core.coroutines.CoroutineFeature
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

internal class DefaultRatingPlayersComponent(
    private val ratedUsersCache: RatedUsersCache,
    dispatchers: KotlinDispatchers
) : RatingPlayersComponent,
    CoroutineFeature by CoroutineFeature.Default(dispatchers.Main).withTimings() {
    override val model = MutableStateFlow(
        RatingPlayersComponent.Model(
            userRatings = emptyList(),
            sort = UsersRatingsSort.TotalRating(true),
            isLoading = false
        )
    )

    override fun onSortClicked(isRightClick: Boolean) {
        val newSort = SORTS.nextAfter(model.value.sort, backwards = isRightClick)
        model.update { currentModel ->
            currentModel.copy(
                userRatings = currentModel.userRatings.sortedBy(newSort.isAsc) { ratedUser -> ratedUser.ratingTotal },
                sort = newSort
            )
        }
    }

    init {
        launch(dispatchers.IO) {
            model.update { currentModel -> currentModel.copy(isLoading = true) }
            val userRatings = ratedUsersCache.fetchUsersTotalRating()
            model.update { currentModel -> currentModel.copy(userRatings = userRatings) }
            onSortClicked()
            model.update { currentModel -> currentModel.copy(isLoading = false) }
        }
    }

    companion object {
        private val SORTS = listOf(
            UsersRatingsSort.TotalRating(true),
            UsersRatingsSort.TotalRating(false),
        )
    }
}
