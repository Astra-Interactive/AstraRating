package com.astrainteractive.astrarating.domain.api

import com.astrainteractive.astrarating.modules.TranslationProvider
import com.astrainteractive.astrarating.utils.PluginTranslation

private val translation: PluginTranslation
    get() = TranslationProvider.value

enum class UserRatingsSort(val desc: String) {
    PLAYER_ASC(translation.sortPlayerASC),
    PLAYER_DESC(translation.sortPlayerDESC),
    DATE_DESC(translation.sortDateDESC),
    DATE_ASC(translation.sortDateASC),
    RATING_DESC(translation.sortRatingDESC),
    RATING_ASC(translation.sortRatingASC),
}

