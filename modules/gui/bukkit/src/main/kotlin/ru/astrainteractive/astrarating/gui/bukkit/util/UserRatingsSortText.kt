package ru.astrainteractive.astrarating.gui.bukkit.util

import ru.astrainteractive.astralibs.localization.text.LocalizedText
import ru.astrainteractive.astrarating.core.settings.model.AstraRatingTranslation
import ru.astrainteractive.astrarating.gui.api.rating.player.model.UserRatingsSort

internal fun selectUserRatingsSortText(sort: UserRatingsSort, translation: AstraRatingTranslation): LocalizedText {
    return when (sort) {
        is UserRatingsSort.Date -> translation.sort.date
        is UserRatingsSort.Player -> translation.sort.player
        is UserRatingsSort.Rating -> translation.sort.rating
    }
}
