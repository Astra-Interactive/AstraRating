@file:Suppress("Filename")

package ru.astrainteractive.astrarating.gui.util

import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import ru.astrainteractive.astrarating.core.PluginTranslation
import ru.astrainteractive.astrarating.dto.UserDTO
import ru.astrainteractive.astrarating.model.UserRatingsSort
import ru.astrainteractive.astrarating.model.UsersRatingsSort
import java.util.UUID

val UserDTO.offlinePlayer: OfflinePlayer
    get() = Bukkit.getOfflinePlayer(UUID.fromString(minecraftUUID))

val UserDTO.normalName: String
    get() = Bukkit.getOfflinePlayer(UUID.fromString(minecraftUUID)).name
        ?: Bukkit.getOfflinePlayer(minecraftName)?.name ?: minecraftName

val UsersRatingsSort.desc: TranslationDesc
    get() = when (this) {
        UsersRatingsSort.ASC -> TranslationDesc(PluginTranslation::sortASC)
        UsersRatingsSort.DESC -> TranslationDesc(PluginTranslation::sortDESC)
    }

val UserRatingsSort.desc: TranslationDesc
    get() = when (this) {
        UserRatingsSort.PLAYER_ASC -> TranslationDesc(PluginTranslation::sortPlayerASC)
        UserRatingsSort.PLAYER_DESC -> TranslationDesc(PluginTranslation::sortPlayerDESC)
        UserRatingsSort.DATE_DESC -> TranslationDesc(PluginTranslation::sortDateDESC)
        UserRatingsSort.DATE_ASC -> TranslationDesc(PluginTranslation::sortDateASC)
        UserRatingsSort.RATING_DESC -> TranslationDesc(PluginTranslation::sortRatingDESC)
        UserRatingsSort.RATING_ASC -> TranslationDesc(PluginTranslation::sortRatingASC)
    }
