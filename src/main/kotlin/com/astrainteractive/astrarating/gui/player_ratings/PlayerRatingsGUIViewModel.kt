package com.astrainteractive.astrarating.gui.player_ratings

import ru.astrainteractive.astralibs.utils.next
import com.astrainteractive.astrarating.domain.api.IRatingAPI
import com.astrainteractive.astrarating.domain.api.UserRatingsSort
import com.astrainteractive.astrarating.domain.entities.UserAndRating
import com.astrainteractive.astrarating.modules.DatabaseApiModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bukkit.OfflinePlayer
import ru.astrainteractive.astralibs.async.BukkitMain
import ru.astrainteractive.astralibs.async.PluginScope

/**
 * MVVM technique
 */
class PlayerRatingsGUIViewModel(val player: OfflinePlayer) {
    private val databaseApi: IRatingAPI
        get() = DatabaseApiModule.value

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
        PluginScope.launch {
            _userRatings.value = databaseApi.fetchUserRatings(player.name?:"NULL") ?: emptyList()
        }
    }


    fun onDisable() {

    }

    fun onDeleteClicked(item: UserAndRating, onResult:()->Unit) {
        PluginScope.launch {
            databaseApi.deleteUserRating(item.rating)
            val list = databaseApi.fetchUserRatings(player.name?:"NULL") ?: emptyList()
            _userRatings.emit(list)
            _userRatings.value = list
            withContext(Dispatchers.BukkitMain){
                onResult()
            }
        }
    }
}