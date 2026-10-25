package ru.astrainteractive.astrarating.gui.api.rating.player.util

import ru.astrainteractive.astrarating.data.dao.rating.model.UserRating
import ru.astrainteractive.astrarating.gui.api.rating.player.model.UserRatingsSort
import ru.astrainteractive.astrarating.gui.api.util.sortedBy

internal fun List<UserRating>.sortedBy(sort: UserRatingsSort): List<UserRating> {
    return when (sort) {
        is UserRatingsSort.Date -> sortedBy(sort.isAsc, UserRating::time)
        is UserRatingsSort.Player -> sortedBy(sort.isAsc) { rating -> rating.userCreatedReport?.minecraftName }
        is UserRatingsSort.Rating -> sortedBy(sort.isAsc, UserRating::rating)
    }
}
