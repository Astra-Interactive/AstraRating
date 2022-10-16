package com.astrainteractive.astrarating.domain.api

import com.astrainteractive.astrarating.modules.TranslationProvider
import com.astrainteractive.astrarating.utils.PluginTranslation

private val translation: PluginTranslation
    get() = TranslationProvider.value

enum class UsersRatingsSort(val desc: String) {
    ASC(translation.sortASC),
    DESC(translation.sortDESC),
}

