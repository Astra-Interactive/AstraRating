package com.astrainteractive.astrarating.api

import com.astrainteractive.astrarating.utils.Translation


enum class UserRatingsSort(val desc: String) {
    PLAYER_ASC(Translation.sortPlayerASC),
    PLAYER_DESC(Translation.sortPlayerDESC),
    DATE_DESC(Translation.sortDateDESC),
    DATE_ASC(Translation.sortDateASC),
    RATING_DESC(Translation.sortRatingDESC),
    RATING_ASC(Translation.sortRatingASC),
}

