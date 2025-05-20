package ru.astrainteractive.astrarating.feature.allrating

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.astrainteractive.astralibs.async.CoroutineFeature
import ru.astrainteractive.astrarating.feature.allrating.data.AllRatingsCachedRepository
import ru.astrainteractive.astrarating.feature.allrating.model.AllRatingsState
import ru.astrainteractive.astrarating.model.UsersRatingsSort
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import ru.astrainteractive.klibs.mikro.core.util.next

class AllRatingsFeature internal constructor(
    private val repository: AllRatingsCachedRepository,
    dispatchers: KotlinDispatchers
) : CoroutineFeature by CoroutineFeature.Default(dispatchers.Main) {
    val model = MutableStateFlow(AllRatingsState())

    fun onSortClicked() {
        val nextSort = model.value.sort.next(UsersRatingsSort.entries.toTypedArray())
        val userRatings = model.value.userRatings
        val sortedUserRatings = if (nextSort == UsersRatingsSort.ASC) {
            userRatings.sortedBy { it.ratingTotal }
        } else {
            userRatings.sortedByDescending { it.ratingTotal }
        }
        model.update {
            it.copy(
                userRatings = sortedUserRatings,
                sort = nextSort
            )
        }
    }

    init {
        launch(dispatchers.IO) {
            model.update { it.copy(isLoading = true) }
            val userRatings = repository.fetchUsersTotalRating()
            model.update {
                it.copy(userRatings = userRatings)
            }
            onSortClicked()
            model.update { it.copy(isLoading = false) }
        }
    }
}
