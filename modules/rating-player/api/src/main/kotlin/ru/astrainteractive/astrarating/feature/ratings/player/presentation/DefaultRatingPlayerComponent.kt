package ru.astrainteractive.astrarating.feature.ratings.player.presentation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.astrainteractive.astralibs.async.CoroutineFeature
import ru.astrainteractive.astrarating.data.dao.RatingDao
import ru.astrainteractive.astrarating.data.exposed.dto.UserRatingDTO
import ru.astrainteractive.astrarating.data.exposed.model.UserRatingsSort
import ru.astrainteractive.astrarating.feature.ratings.player.domain.RatingSortUseCase
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import java.util.UUID
import kotlin.math.absoluteValue

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

    override fun onSortClicked(isRightClick: Boolean) {
        launch(dispatchers.IO) {
            val sorts = listOf(
                UserRatingsSort.Rating(true),
                UserRatingsSort.Rating(false),
                UserRatingsSort.Player(true),
                UserRatingsSort.Player(false),
                UserRatingsSort.Date(true),
                UserRatingsSort.Date(false)
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
            val input = RatingSortUseCase.Input(
                ratings = model.value.allRatings,
                sort = newSortType
            )
            val result = ratingSortUseCase.invoke(input)
            model.update {
                it.copy(
                    sort = newSortType,
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
