package ru.astrainteractive.astrarating.gui.bukkit.util

import ru.astrainteractive.astralibs.localization.text.LocalizedText
import ru.astrainteractive.astrarating.core.settings.model.AstraRatingTranslation
import ru.astrainteractive.astrarating.gui.api.rating.players.model.UsersRatingsSort

internal fun UsersRatingsSort.toText(translation: AstraRatingTranslation): LocalizedText = when (this) {
    is UsersRatingsSort.TotalRating -> translation.sort.rating
}
