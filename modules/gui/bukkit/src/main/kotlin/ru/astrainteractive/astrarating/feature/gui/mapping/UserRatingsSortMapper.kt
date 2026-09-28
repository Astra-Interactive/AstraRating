package ru.astrainteractive.astrarating.feature.gui.mapping

import ru.astrainteractive.astralibs.localization.text.LocalizedText
import ru.astrainteractive.astrarating.core.settings.AstraRatingTranslation
import ru.astrainteractive.astrarating.data.exposed.model.UserRatingsSort
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.getValue

class UserRatingsSortMapper(translationKrate: CachedKrate<AstraRatingTranslation>) {
    private val translation by translationKrate

    fun toText(sort: UserRatingsSort): LocalizedText {
        return when (sort) {
            is UserRatingsSort.Date -> translation.sort.date
            is UserRatingsSort.Player -> translation.sort.player
            is UserRatingsSort.Rating -> translation.sort.rating
        }
    }
}
