package ru.astrainteractive.astrarating.gui.api.rating.change.util

import ru.astrainteractive.astrarating.core.settings.model.AstraRatingConfig

internal fun AstraRatingConfig.isRatingMessageLengthAllowed(message: String): Boolean {
    return message.length in minMessageLength..maxMessageLength
}
