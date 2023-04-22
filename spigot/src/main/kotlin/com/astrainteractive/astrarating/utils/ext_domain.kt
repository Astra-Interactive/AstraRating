package com.astrainteractive.astrarating.utils

import com.astrainteractive.astrarating.models.UserRatingsSort
import com.astrainteractive.astrarating.models.UsersRatingsSort
import com.astrainteractive.astrarating.dto.UserDTO
import com.astrainteractive.astrarating.modules.ServiceLocator
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import ru.astrainteractive.astralibs.di.getValue
import java.util.*


private val translation by ServiceLocator.translation

val UserDTO.offlinePlayer: OfflinePlayer
    get() = Bukkit.getOfflinePlayer(UUID.fromString(minecraftUUID))

val UserDTO.normalName: String
    get() = Bukkit.getOfflinePlayer(UUID.fromString(minecraftUUID)).name
        ?: Bukkit.getOfflinePlayer(minecraftName)?.name ?: minecraftName

val UsersRatingsSort.desc:String
    get() = when(this){
        UsersRatingsSort.ASC -> translation.sortASC
        UsersRatingsSort.DESC -> translation.sortDESC
    }

val UserRatingsSort.desc:String
    get() = when(this){
        UserRatingsSort.PLAYER_ASC -> translation.sortPlayerASC
        UserRatingsSort.PLAYER_DESC -> translation.sortPlayerDESC
        UserRatingsSort.DATE_DESC -> translation.sortDateDESC
        UserRatingsSort.DATE_ASC -> translation.sortDateASC
        UserRatingsSort.RATING_DESC -> translation.sortRatingDESC
        UserRatingsSort.RATING_ASC -> translation.sortRatingASC
    }