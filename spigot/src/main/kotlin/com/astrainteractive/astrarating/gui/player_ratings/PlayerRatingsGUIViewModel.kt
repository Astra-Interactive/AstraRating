package com.astrainteractive.astrarating.gui.player_ratings

import ru.astrainteractive.astralibs.utils.next
import com.astrainteractive.astrarating.domain.api.IRatingAPI
import com.astrainteractive.astrarating.domain.entities.UserRatingsSort
import com.astrainteractive.astrarating.domain.entities.tables.dto.UserAndRating
import com.astrainteractive.astrarating.modules.DatabaseApiModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bukkit.OfflinePlayer
import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astralibs.async.BukkitMain
import ru.astrainteractive.astralibs.async.PluginScope

/**
 * MVVM technique
 */
class PlayerRatingsGUIViewModel(val player: OfflinePlayer): AsyncComponent() {
    private val databaseApi: IRatingAPI
        get() = DatabaseApiModule.value

    private val _userRatings = MutableStateFlow<List<UserAndRating>>(emptyList())
    val userRatings: StateFlow<List<UserAndRating>>
        get() = _userRatings

    private val _sort = MutableStateFlow(UserRatingsSort.DATE_DESC)
    val sort: StateFlow<UserRatingsSort>
        get() = _sort

    fun onSortClicked() {

        componentScope.launch(Dispatchers.IO) {
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
        componentScope.launch(Dispatchers.IO) {
            _userRatings.value = databaseApi.fetchUserRatings(player.name?:"NULL") ?: emptyList()
        }
    }
    fun onDeleteClicked(item: UserAndRating) {
        PluginScope.launch(Dispatchers.IO) {
            databaseApi.deleteUserRating(item.rating)
            val list = databaseApi.fetchUserRatings(player.name?:"NULL") ?: emptyList()
            _userRatings.value = list
        }
    }
}