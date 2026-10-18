package ru.astrainteractive.astrarating.gui.api.rating.player.component

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.astrainteractive.astralibs.coroutines.withTimings
import ru.astrainteractive.astrarating.data.dao.rating.api.RatingRepository
import ru.astrainteractive.astrarating.data.dao.rating.model.UserRating
import ru.astrainteractive.astrarating.gui.api.rating.player.model.UserRatingsSort
import ru.astrainteractive.astrarating.gui.api.rating.player.util.sortedBy
import ru.astrainteractive.astrarating.gui.api.util.nextAfter
import ru.astrainteractive.klibs.mikro.core.coroutines.CoroutineFeature
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import ru.astrainteractive.klibs.mikro.core.logging.JUtiltLogger
import ru.astrainteractive.klibs.mikro.core.logging.Logger
import java.util.UUID

internal class DefaultRatingPlayerComponent(
    playerName: String,
    playerUUID: UUID,
    private val ratingRepository: RatingRepository,
    private val dispatchers: KotlinDispatchers
) : RatingPlayerComponent,
    CoroutineFeature by CoroutineFeature.Default(dispatchers.Main).withTimings(),
    Logger by JUtiltLogger("AstraRating-RatingPlayerComponent") {
    override val model = MutableStateFlow(
        RatingPlayerComponent.Model(
            playerName = playerName,
            playerUUID = playerUUID,
            allRatings = emptyList(),
            sort = UserRatingsSort.Rating(true),
            isLoading = false
        )
    )

    override fun onSortClicked(isRightClick: Boolean) {
        launch(dispatchers.IO) {
            val newSort = SORTS.nextAfter(model.value.sort, backwards = isRightClick)
            model.update { currentModel ->
                currentModel.copy(
                    sort = newSort,
                    allRatings = currentModel.allRatings.sortedBy(newSort)
                )
            }
        }
    }

    private fun reload() {
        launch(dispatchers.IO) {
            model.update { currentModel -> currentModel.copy(isLoading = true) }
            val userRatings = ratingRepository.fetchUserRatings(model.value.playerUUID)
                .onFailure { throwable -> error(throwable) { "#reload ratings of ${model.value.playerName} failed" } }
                .getOrDefault(emptyList())
            model.update { currentModel -> currentModel.copy(allRatings = userRatings) }
            onSortClicked()
            model.update { currentModel -> currentModel.copy(isLoading = false) }
        }
    }

    override fun onDeleteClicked(item: UserRating) {
        launch(dispatchers.IO) {
            model.update { currentModel -> currentModel.copy(isLoading = true) }
            ratingRepository.deleteUserRating(item)
                .onFailure { throwable -> error(throwable) { "#onDeleteClicked rating ${item.id} failed" } }
            model.update { currentModel -> currentModel.copy(isLoading = false) }
            reload()
        }
    }

    init {
        reload()
    }

    companion object {
        private val SORTS = listOf(
            UserRatingsSort.Rating(true),
            UserRatingsSort.Rating(false),
            UserRatingsSort.Player(true),
            UserRatingsSort.Player(false),
            UserRatingsSort.Date(true),
            UserRatingsSort.Date(false)
        )
    }
}
