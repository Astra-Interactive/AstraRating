package ru.astrainteractive.astrarating.gui.bukkit.util

import ru.astrainteractive.astralibs.localization.text.LocalizedText
import ru.astrainteractive.astrarating.core.settings.model.AstraRatingTranslation
import ru.astrainteractive.astrarating.gui.api.rating.player.model.UserRatingsSort

internal fun UserRatingsSort.toText(translation: AstraRatingTranslation): LocalizedText = when (this) {
    is UserRatingsSort.Date -> translation.sort.date
    is UserRatingsSort.Player -> translation.sort.player
    is UserRatingsSort.Rating -> translation.sort.rating
}
