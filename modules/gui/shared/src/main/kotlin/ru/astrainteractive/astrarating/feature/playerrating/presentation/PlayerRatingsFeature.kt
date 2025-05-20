package ru.astrainteractive.astrarating.feature.playerrating.presentation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.astrainteractive.astralibs.async.CoroutineFeature
import ru.astrainteractive.astrarating.api.rating.api.RatingDBApi
import ru.astrainteractive.astrarating.dto.UserRatingDTO
import ru.astrainteractive.astrarating.feature.playerrating.domain.SortRatingUseCase
import ru.astrainteractive.astrarating.feature.playerrating.presentation.model.PlayerRatingsState
import ru.astrainteractive.astrarating.model.UserRatingsSort
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import ru.astrainteractive.klibs.mikro.core.util.next
import java.util.UUID

class PlayerRatingsFeature internal constructor(
    playerName: String,
    playerUUID: UUID,
    private val dbApi: RatingDBApi,
    private val dispatchers: KotlinDispatchers,
    private val sortRatingUseCase: SortRatingUseCase
) : CoroutineFeature by CoroutineFeature.Default(dispatchers.Main) {
    val state = MutableStateFlow(
        value = PlayerRatingsState(
            playerName = playerName,
            playerUUID = playerUUID
        )
    )

    fun onSortClicked() {
        launch(dispatchers.IO) {
            val sort = state.value.sort.next(UserRatingsSort.entries.toTypedArray())
            val input = SortRatingUseCase.Input(
                ratings = state.value.allRatings,
                sort = sort
            )
            val result = sortRatingUseCase.invoke(input)
            state.update {
                it.copy(
                    sort = sort,
                    allRatings = result.ratings
                )
            }
        }
    }

    fun onDeleteClicked(item: UserRatingDTO) {
        launch(dispatchers.IO) {
            state.update { it.copy(isLoading = true) }
            dbApi.deleteUserRating(item)
            state.update { it.copy(isLoading = false) }
            reload()
        }
    }

    private fun reload() {
        launch(dispatchers.IO) {
            state.update { it.copy(isLoading = true) }
            val userRatings = dbApi.fetchUserRatings(state.value.playerUUID)
                .onFailure(Throwable::printStackTrace)
                .getOrDefault(emptyList())
            state.update {
                it.copy(allRatings = userRatings)
            }
            onSortClicked()
            state.update { it.copy(isLoading = false) }
        }
    }

    init {
        reload()
    }
}
