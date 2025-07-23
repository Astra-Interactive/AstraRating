@file:Suppress("Filename")

package ru.astrainteractive.astrarating.gui.util

import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import ru.astrainteractive.astrarating.core.settings.AstraRatingTranslation
import ru.astrainteractive.astrarating.data.exposed.dto.UserDTO
import ru.astrainteractive.astrarating.data.exposed.model.UserRatingsSort
import ru.astrainteractive.astrarating.data.exposed.model.UsersRatingsSort
import java.util.UUID

internal val UserDTO.offlinePlayer: OfflinePlayer
    get() = Bukkit.getOfflinePlayer(UUID.fromString(minecraftUUID))

internal val UserDTO.normalName: String
    get() = Bukkit.getOfflinePlayer(UUID.fromString(minecraftUUID)).name
        ?: Bukkit.getOfflinePlayer(minecraftName)?.name ?: minecraftName

internal val UsersRatingsSort.desc: TranslationDesc
    get() = when (this) {
        UsersRatingsSort.ASC -> TranslationDesc(AstraRatingTranslation::sortASC)
        UsersRatingsSort.DESC -> TranslationDesc(AstraRatingTranslation::sortDESC)
    }

internal val UserRatingsSort.desc: TranslationDesc
    get() = when (this) {
        UserRatingsSort.PLAYER_ASC -> TranslationDesc(AstraRatingTranslation::sortPlayerASC)
        UserRatingsSort.PLAYER_DESC -> TranslationDesc(AstraRatingTranslation::sortPlayerDESC)
        UserRatingsSort.DATE_DESC -> TranslationDesc(AstraRatingTranslation::sortDateDESC)
        UserRatingsSort.DATE_ASC -> TranslationDesc(AstraRatingTranslation::sortDateASC)
        UserRatingsSort.RATING_DESC -> TranslationDesc(AstraRatingTranslation::sortRatingDESC)
        UserRatingsSort.RATING_ASC -> TranslationDesc(AstraRatingTranslation::sortRatingASC)
    }
