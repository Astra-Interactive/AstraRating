package com.astrainteractive.astrarating.gui.playerratings

import com.astrainteractive.astrarating.dto.UserAndRating
import com.astrainteractive.astrarating.gui.playerratings.di.PlayerRatingGuiModule
import com.astrainteractive.astrarating.models.UserRatingsSort
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.bukkit.OfflinePlayer
import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astralibs.getValue
import ru.astrainteractive.astralibs.utils.next

/**
 * MVVM technique
 */
class PlayerRatingsGUIViewModel(
    val player: OfflinePlayer,
    module: PlayerRatingGuiModule
) : AsyncComponent() {
    private val databaseApi by module.dbApi
    private val dispatchers by module.dispatchers

    private val _userRatings = MutableStateFlow<List<UserAndRating>>(emptyList())
    val userRatings: StateFlow<List<UserAndRating>>
        get() = _userRatings

    private val _sort = MutableStateFlow(UserRatingsSort.values().last())
    val sort: StateFlow<UserRatingsSort>
        get() = _sort

    fun onSortClicked() {
        componentScope.launch(dispatchers.IO) {
            _sort.value = sort.value.next()
            _userRatings.value = when (sort.value) {
                UserRatingsSort.DATE_ASC -> _userRatings.value.sortedBy { it.rating.time }
                UserRatingsSort.DATE_DESC -> _userRatings.value.sortedByDescending { it.rating.time }
                UserRatingsSort.PLAYER_ASC -> _userRatings.value.sortedBy { it.userCreatedReport.id }
                UserRatingsSort.PLAYER_DESC -> _userRatings.value.sortedByDescending { it.userCreatedReport.id }
                UserRatingsSort.RATING_ASC -> _userRatings.value.sortedBy { it.rating.rating }
                UserRatingsSort.RATING_DESC -> _userRatings.value.sortedBy { it.rating.rating }
            }
        }
    }

    init {
        componentScope.launch(dispatchers.IO) {
            _userRatings.value = databaseApi.fetchUserRatings(player.name ?: "NULL").getOrDefault(emptyList())
        }
    }

    fun onDeleteClicked(item: UserAndRating) {
        componentScope.launch(dispatchers.IO) {
            databaseApi.deleteUserRating(item.rating)
            val list = databaseApi.fetchUserRatings(player.name ?: "NULL").getOrDefault(emptyList())
            _userRatings.value = list
            onSortClicked()
        }
    }
}
