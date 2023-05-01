package com.astrainteractive.astrarating.gui.ratings

import com.astrainteractive.astrarating.dto.UserAndRating
import com.astrainteractive.astrarating.gui.ratings.di.RatingsGUIModule
import com.astrainteractive.astrarating.models.UsersRatingsSort
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astralibs.getValue
import ru.astrainteractive.astralibs.utils.next

/**
 * MVVM technique
 */
class RatingsGUIViewModel(module: RatingsGUIModule) : AsyncComponent() {
    private val databaseApi by module.dbApi
    private val dispatchers by module.dispatchers

    companion object {
        fun getHead(playerName: String) = Companion.getHead(Bukkit.getOfflinePlayer(playerName))
        fun getHead(player: OfflinePlayer): ItemStack {
            val item = ItemStack(Material.PLAYER_HEAD)
            val meta: SkullMeta = item.itemMeta as SkullMeta
            meta.owningPlayer = Bukkit.getOfflinePlayer(player.uniqueId)
            item.itemMeta = meta
            return item
        }
    }

    private val _userRatings = MutableStateFlow<List<UserAndRating>>(emptyList())
    val userRatings: StateFlow<List<UserAndRating>>
        get() = _userRatings

    private val _sort = MutableStateFlow(UsersRatingsSort.values().last())
    val sort: StateFlow<UsersRatingsSort>
        get() = _sort

    fun onSortClicked() {
        _sort.value = sort.value.next()
        if (sort.value == UsersRatingsSort.ASC) {
            _userRatings.value = _userRatings.value.sortedBy { it.rating.rating }
        } else {
            _userRatings.value = _userRatings.value.sortedByDescending { it.rating.rating }
        }
    }

    init {
        componentScope.launch(dispatchers.IO) {
            _userRatings.value = databaseApi.fetchUsersTotalRating().getOrDefault(emptyList())
            onSortClicked()
        }
    }
}
