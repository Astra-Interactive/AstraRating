package ru.astrainteractive.astrarating.gui.api.rating.player.util

import ru.astrainteractive.astrarating.core.util.sortedBy
import ru.astrainteractive.astrarating.data.dao.rating.model.UserRating
import ru.astrainteractive.astrarating.gui.api.rating.player.model.UserRatingsSort

internal fun List<UserRating>.sortedBy(sort: UserRatingsSort): List<UserRating> = when (sort) {
    is UserRatingsSort.Date -> sortedBy(sort.isAsc) { rating -> rating.time }
    is UserRatingsSort.Player -> sortedBy(sort.isAsc) { rating -> rating.userCreatedReport?.minecraftName }
    is UserRatingsSort.Rating -> sortedBy(sort.isAsc) { rating -> rating.rating }
}
