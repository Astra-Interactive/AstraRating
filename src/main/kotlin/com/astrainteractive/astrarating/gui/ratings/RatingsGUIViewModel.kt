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
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.util.*

/**
 * MVVM technique
 */
class RatingsGUIViewModel {
    companion object {
        private val _skinByName = MutableStateFlow<MutableMap<String, Pair<String, String>>>(mutableMapOf())
        suspend fun rememberSkin(name: String?) {
            name ?: return
            if (_skinByName.value.contains(name)) return
            getSkinByName(name)?.let {
                _skinByName.value[name] = it
            }
        }


        fun getHead(name: String): ItemStack {
            val item = ItemStack(Material.PLAYER_HEAD)
            _skinByName.value[name]?.let { skin ->
                val meta: SkullMeta = item.itemMeta as SkullMeta
                val profile = GameProfile(UUID.randomUUID(), null)
                profile.properties.put("textures", Property("textures", skin?.first, skin?.second))
                setDeclaredField(meta::class.java, meta, "profile", profile)
                item.itemMeta = meta
            }
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
            _userRatings.value.forEach {
                rememberSkin(it.reportedPlayer?.minecraftName)
                rememberSkin(it.userCreatedReport.minecraftName)
            }
        }
    }


    fun onDisable() {

    }
}