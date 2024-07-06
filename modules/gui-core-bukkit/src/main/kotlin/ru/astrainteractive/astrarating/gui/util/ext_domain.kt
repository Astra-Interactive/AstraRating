@file:Suppress("Filename")

package ru.astrainteractive.astrarating.gui.util

import java.util.UUID
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import ru.astrainteractive.astrarating.core.PluginTranslation
import ru.astrainteractive.astrarating.dto.UserDTO
import ru.astrainteractive.astrarating.model.UserRatingsSort
import ru.astrainteractive.astrarating.model.UsersRatingsSort

internal val UserDTO.offlinePlayer: OfflinePlayer
    get() = Bukkit.getOfflinePlayer(UUID.fromString(minecraftUUID))

internal val UserDTO.normalName: String
    get() = Bukkit.getOfflinePlayer(UUID.fromString(minecraftUUID)).name
        ?: Bukkit.getOfflinePlayer(minecraftName)?.name ?: minecraftName

internal val UsersRatingsSort.desc: TranslationDesc
    get() = when (this) {
        UsersRatingsSort.ASC -> TranslationDesc(PluginTranslation::sortASC)
        UsersRatingsSort.DESC -> TranslationDesc(PluginTranslation::sortDESC)
    }

internal val UserRatingsSort.desc: TranslationDesc
    get() = when (this) {
        UserRatingsSort.PLAYER_ASC -> TranslationDesc(PluginTranslation::sortPlayerASC)
        UserRatingsSort.PLAYER_DESC -> TranslationDesc(PluginTranslation::sortPlayerDESC)
        UserRatingsSort.DATE_DESC -> TranslationDesc(PluginTranslation::sortDateDESC)
        UserRatingsSort.DATE_ASC -> TranslationDesc(PluginTranslation::sortDateASC)
        UserRatingsSort.RATING_DESC -> TranslationDesc(PluginTranslation::sortRatingDESC)
        UserRatingsSort.RATING_ASC -> TranslationDesc(PluginTranslation::sortRatingASC)
    }
