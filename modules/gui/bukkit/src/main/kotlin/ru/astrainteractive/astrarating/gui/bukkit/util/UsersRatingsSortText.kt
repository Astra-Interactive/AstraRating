package ru.astrainteractive.astrarating.gui.bukkit.util

import ru.astrainteractive.astralibs.localization.text.LocalizedText
import ru.astrainteractive.astrarating.core.settings.model.AstraRatingTranslation
import ru.astrainteractive.astrarating.gui.api.rating.ranking.model.UsersRatingsSort

internal fun selectUsersRatingsSortText(sort: UsersRatingsSort, translation: AstraRatingTranslation): LocalizedText {
    return when (sort) {
        is UsersRatingsSort.TotalRating -> translation.sort.rating
    }
}
