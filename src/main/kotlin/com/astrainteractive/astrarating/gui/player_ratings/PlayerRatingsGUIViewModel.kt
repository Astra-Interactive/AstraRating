package com.astrainteractive.astrarating.gui.player_ratings

import com.astrainteractive.astralibs.async.AsyncHelper
import com.astrainteractive.astrarating.api.DatabaseApi
import com.astrainteractive.astrarating.api.UserRatingsSort
import com.astrainteractive.astrarating.sqldatabase.UserAndRating
import com.astrainteractive.astrarating.utils.next
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.bukkit.OfflinePlayer
import java.util.*

/**
 * MVVM technique
 */
class PlayerRatingsGUIViewModel(val player: OfflinePlayer) {

    private val _userRatings = MutableStateFlow<List<UserAndRating>>(emptyList())
    val userRatings: StateFlow<List<UserAndRating>>
        get() = _userRatings

    private val _sort = MutableStateFlow(UserRatingsSort.DATE_DESC)
    val sort: StateFlow<UserRatingsSort>
        get() = _sort

    fun onSortClicked() {
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

    init {
        AsyncHelper.launch {
            _userRatings.value = DatabaseApi.fetchUserRatings(player) ?: emptyList()
        }
    }


    fun onDisable() {

    }

    fun onDeleteClicked(item: UserAndRating,onResult:()->Unit) {
        AsyncHelper.launch {
            DatabaseApi.deleteUserRating(item.rating)
            val list = DatabaseApi.fetchUserRatings(player) ?: emptyList()
            _userRatings.emit(list)
            _userRatings.value = list
            AsyncHelper.callSyncMethod(onResult)
        }
    }
}