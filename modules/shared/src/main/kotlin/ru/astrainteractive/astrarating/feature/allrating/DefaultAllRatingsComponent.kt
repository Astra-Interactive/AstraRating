package ru.astrainteractive.astrarating.feature.allrating

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astrarating.api.rating.api.RatingDBApi
import ru.astrainteractive.astrarating.model.UsersRatingsSort
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import ru.astrainteractive.klibs.mikro.core.util.next

class DefaultAllRatingsComponent(
    private val dbApi: RatingDBApi,
    dispatchers: KotlinDispatchers
) : AllRatingsComponent, AsyncComponent() {
    override val model = MutableStateFlow(AllRatingsComponent.Model())

    override fun onSortClicked() {
        val nextSort = model.value.sort.next(UsersRatingsSort.values())
        val userRatings = model.value.userRatings
        val sortedUserRatings = if (nextSort == UsersRatingsSort.ASC) {
            userRatings.sortedBy { it.rating.rating }
        } else {
            userRatings.sortedByDescending { it.rating.rating }
        }
        model.update {
            it.copy(
                userRatings = sortedUserRatings,
                sort = nextSort
            )
        }
    }

    init {
        componentScope.launch(dispatchers.IO) {
            model.update { it.copy(isLoading = true) }
            val userRatings = dbApi.fetchUsersTotalRating().getOrDefault(emptyList())
            model.update {
                it.copy(userRatings = userRatings)
            }
            onSortClicked()
            model.update { it.copy(isLoading = false) }
        }
    }
}
