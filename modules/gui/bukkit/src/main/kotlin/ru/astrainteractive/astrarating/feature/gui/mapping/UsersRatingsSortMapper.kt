package ru.astrainteractive.astrarating.feature.gui.mapping

import ru.astrainteractive.astralibs.localization.text.LocalizedText
import ru.astrainteractive.astrarating.core.settings.AstraRatingTranslation
import ru.astrainteractive.astrarating.data.exposed.model.UsersRatingsSort
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.getValue

class UsersRatingsSortMapper(translationKrate: CachedKrate<AstraRatingTranslation>) {
    private val translation by translationKrate

    fun toText(sort: UsersRatingsSort): LocalizedText {
        return when (sort) {
            is UsersRatingsSort.TotalRating -> translation.sort.rating
        }
    }
}
