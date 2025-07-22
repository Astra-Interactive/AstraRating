package ru.astrainteractive.astrarating.feature.ratings.player.presentation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.astrainteractive.astralibs.async.CoroutineFeature
import ru.astrainteractive.astrarating.api.rating.api.RatingDao
import ru.astrainteractive.astrarating.dto.UserRatingDTO
import ru.astrainteractive.astrarating.feature.ratings.player.domain.RatingSortUseCase
import ru.astrainteractive.astrarating.model.UserRatingsSort
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import ru.astrainteractive.klibs.mikro.core.util.next
import java.util.UUID

internal class DefaultRatingPlayerComponent(
    playerName: String,
    playerUUID: UUID,
    private val dbApi: RatingDao,
    private val dispatchers: KotlinDispatchers,
    private val ratingSortUseCase: RatingSortUseCase
) : RatingPlayerComponent, CoroutineFeature by CoroutineFeature.Default(dispatchers.Main) {
    override val model = MutableStateFlow(
        RatingPlayerComponent.Model(
            playerName = playerName,
            playerUUID = playerUUID
        )
    )

    override fun onSortClicked() {
        launch(dispatchers.IO) {
            val sort = model.value.sort.next(UserRatingsSort.entries.toTypedArray())
            val input = RatingSortUseCase.Input(
                ratings = model.value.allRatings,
                sort = sort
            )
            val result = ratingSortUseCase.invoke(input)
            model.update {
                it.copy(
                    sort = sort,
                    allRatings = result.ratings
                )
            }
        }
    }

    override fun onDeleteClicked(item: UserRatingDTO) {
        launch(dispatchers.IO) {
            model.update { it.copy(isLoading = true) }
            dbApi.deleteUserRating(item)
            model.update { it.copy(isLoading = false) }
            reload()
        }
    }

    private fun reload() {
        launch(dispatchers.IO) {
            model.update { it.copy(isLoading = true) }
            val userRatings = dbApi.fetchUserRatings(model.value.playerUUID)
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
