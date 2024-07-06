package ru.astrainteractive.astrarating.feature.playerrating.presentation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astrarating.api.rating.api.RatingDBApi
import ru.astrainteractive.astrarating.dto.UserRatingDTO
import ru.astrainteractive.astrarating.feature.playerrating.domain.SortRatingUseCase
import ru.astrainteractive.astrarating.model.UserRatingsSort
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import ru.astrainteractive.klibs.mikro.core.util.next

internal class DefaultPlayerRatingsComponent(
    playerName: String,
    private val dbApi: RatingDBApi,
    private val dispatchers: KotlinDispatchers,
    private val sortRatingUseCase: SortRatingUseCase
) : PlayerRatingsComponent, AsyncComponent() {
    override val model = MutableStateFlow(
        PlayerRatingsComponent.Model(playerName = playerName)
    )

    override fun onSortClicked() {
        componentScope.launch(dispatchers.IO) {
            val sort = model.value.sort.next(UserRatingsSort.entries.toTypedArray())
            val input = SortRatingUseCase.Input(
                ratings = model.value.allRatings,
                sort = sort
            )
            val result = sortRatingUseCase.invoke(input)
            model.update {
                it.copy(
                    sort = sort,
                    allRatings = result.ratings
                )
            }
        }
    }

    override fun onDeleteClicked(item: UserRatingDTO) {
        componentScope.launch(dispatchers.IO) {
            model.update { it.copy(isLoading = true) }
            dbApi.deleteUserRating(item)
            model.update { it.copy(isLoading = false) }
            reload()
        }
    }

    private fun reload() {
        componentScope.launch(dispatchers.IO) {
            model.update { it.copy(isLoading = true) }
            val playerName = model.value.playerName
            val userRatings = dbApi.fetchUserRatings(playerName)
                .onFailure(Throwable::printStackTrace)
                .getOrDefault(emptyList())
            model.update {
                it.copy(allRatings = userRatings)
            }
            onSortClicked()
            model.update { it.copy(isLoading = false) }
        }
    }

    init {
        reload()
    }
}
