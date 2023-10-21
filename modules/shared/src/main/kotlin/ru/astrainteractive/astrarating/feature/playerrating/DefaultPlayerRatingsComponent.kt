package ru.astrainteractive.astrarating.feature.playerrating

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astrarating.api.rating.api.RatingDBApi
import ru.astrainteractive.astrarating.dto.UserRatingDTO
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
                UserRatingsSort.DATE_ASC -> previousUserRatings.sortedBy {
                    it.time
                }

                UserRatingsSort.DATE_DESC -> previousUserRatings.sortedByDescending {
                    it.time
                }

                UserRatingsSort.PLAYER_ASC -> previousUserRatings.sortedBy {
                    it.userCreatedReport?.minecraftName
                }

                UserRatingsSort.PLAYER_DESC -> previousUserRatings.sortedByDescending {
                    it.userCreatedReport?.minecraftName
                }

                UserRatingsSort.RATING_ASC -> previousUserRatings.sortedBy {
                    it.rating
                }

                UserRatingsSort.RATING_DESC -> previousUserRatings.sortedBy {
                    it.rating
                }
            }
            model.update {
                it.copy(
                    sort = newSort,
                    userRatings = newUserRatings
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
            val playerModel = model.value.playerModel
            val userRatings = dbApi.fetchUserRatings(playerModel.name).getOrDefault(emptyList())
            model.update {
                it.copy(userRatings = userRatings)
            }
            onSortClicked()
            model.update { it.copy(isLoading = false) }
        }
    }

    init {
        reload()
    }
}
