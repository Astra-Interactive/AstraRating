package com.astrainteractive.astrarating.gui.ratings

import com.astrainteractive.astralibs.async.AsyncHelper
import com.astrainteractive.astrarating.api.DatabaseApi
import com.astrainteractive.astrarating.api.UsersRatingsSort
import com.astrainteractive.astrarating.sqldatabase.entities.UserAndRating
import com.astrainteractive.astrarating.utils.getSkinByName
import com.astrainteractive.astrarating.utils.next
import com.astrainteractive.astrarating.utils.setDeclaredField
import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.util.*

/**
 * MVVM technique
 */
class RatingsGUIViewModel {
    companion object {
        fun getHead(playerName:String) = Companion.getHead(Bukkit.getOfflinePlayer(playerName))
        fun getHead(player:OfflinePlayer): ItemStack {
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

    private val _sort = MutableStateFlow(UsersRatingsSort.DESC)
    val sort: StateFlow<UsersRatingsSort>
        get() = _sort

    fun onSortClicked() {
        _sort.value = sort.value.next()
        if (sort.value == UsersRatingsSort.ASC)
            _userRatings.value = _userRatings.value.sortedBy { it.rating.rating }
        else
            _userRatings.value = _userRatings.value.sortedByDescending { it.rating.rating }
    }

    init {
        AsyncHelper.launch {
            _userRatings.value = DatabaseApi.fetchUsersTotalRating() ?: emptyList()
        }
    }


    fun onDisable() {

    }
}