package ru.astrainteractive.astrarating.core.gui.mapping

import ru.astrainteractive.astralibs.string.StringDesc
import ru.astrainteractive.astrarating.core.settings.AstraRatingTranslation
import ru.astrainteractive.astrarating.data.exposed.model.UserRatingsSort
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.util.getValue

class UserRatingsSortMapper(translationKrate: CachedKrate<AstraRatingTranslation>) {
    private val translation by translationKrate

    fun toStringDesc(sort: UserRatingsSort): StringDesc {
        return when (sort) {
            is UserRatingsSort.Date -> translation.gui.sortDate
            is UserRatingsSort.Player -> translation.gui.sortPlayer
            is UserRatingsSort.Rating -> translation.gui.sortRating
        }
    }
}
