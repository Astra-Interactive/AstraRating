package ru.astrainteractive.astrarating.core.gui.util

import ru.astrainteractive.astralibs.string.StringDesc
import ru.astrainteractive.astrarating.core.settings.AstraRatingTranslation

internal class TranslationDesc(private val provider: (AstraRatingTranslation) -> StringDesc.Raw) {
    fun toString(astraRatingTranslation: AstraRatingTranslation) = provider.invoke(astraRatingTranslation)
}
