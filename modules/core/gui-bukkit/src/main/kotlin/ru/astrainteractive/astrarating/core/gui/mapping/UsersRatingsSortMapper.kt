package ru.astrainteractive.astrarating.core.gui.mapping

import ru.astrainteractive.astralibs.string.StringDesc
import ru.astrainteractive.astrarating.core.settings.AstraRatingTranslation
import ru.astrainteractive.astrarating.data.exposed.model.UserRatingsSort
import ru.astrainteractive.astrarating.data.exposed.model.UsersRatingsSort
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.util.getValue

class UsersRatingsSortMapper(translationKrate: CachedKrate<AstraRatingTranslation>) {
    private val translation by translationKrate

    fun toStringDesc(sort: UsersRatingsSort): StringDesc {
        return when (sort) {
            is UsersRatingsSort.Players -> translation.gui.sortPlayer
        }
    }
}