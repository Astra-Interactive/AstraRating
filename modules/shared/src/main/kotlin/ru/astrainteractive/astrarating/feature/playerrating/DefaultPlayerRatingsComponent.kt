package ru.astrainteractive.astrarating.feature.playerrating

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astrarating.api.rating.api.RatingDBApi
import ru.astrainteractive.astrarating.dto.UserAndRating
import ru.astrainteractive.astrarating.model.PlayerModel
import ru.astrainteractive.astrarating.model.UserRatingsSort
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import ru.astrainteractive.klibs.mikro.core.util.next

class DefaultPlayerRatingsComponent(
    playerModel: PlayerModel,
    private val dbApi: RatingDBApi,
    private val dispatchers: KotlinDispatchers
) : PlayerRatingsComponent, AsyncComponent() {
    override val model = MutableStateFlow(
        PlayerRatingsComponent.Model(playerModel = playerModel)
    )

    override fun onSortClicked() {
        componentScope.launch(dispatchers.IO) {
            val newSort = model.value.sort.next(UserRatingsSort.values())
            val previousUserRatings = model.value.userRatings
            val newUserRatings = when (newSort) {
                UserRatingsSort.DATE_ASC -> previousUserRatings.sortedBy { it.rating.time }
                UserRatingsSort.DATE_DESC -> previousUserRatings.sortedByDescending { it.rating.time }
                UserRatingsSort.PLAYER_ASC -> previousUserRatings.sortedBy { it.userCreatedReport.id }
                UserRatingsSort.PLAYER_DESC -> previousUserRatings.sortedByDescending { it.userCreatedReport.id }
                UserRatingsSort.RATING_ASC -> previousUserRatings.sortedBy { it.rating.rating }
                UserRatingsSort.RATING_DESC -> previousUserRatings.sortedBy { it.rating.rating }
            }
            model.update {
                it.copy(
                    sort = newSort,
                    userRatings = newUserRatings
                )
            }
        }
    }

    override fun onDeleteClicked(item: UserAndRating) {
        componentScope.launch(dispatchers.IO) {
            dbApi.deleteUserRating(item.rating)
        }
    }

    private fun reload() {
        componentScope.launch(dispatchers.IO) {
            val playerModel = model.value.playerModel
            val userRatings = dbApi.fetchUserRatings(playerModel.name).getOrDefault(emptyList())
            model.update {
                it.copy(userRatings = userRatings)
            }
            onSortClicked()
        }
    }

    init {
        reload()
    }
}
