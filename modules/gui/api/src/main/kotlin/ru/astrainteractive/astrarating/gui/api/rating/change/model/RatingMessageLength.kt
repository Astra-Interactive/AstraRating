package ru.astrainteractive.astrarating.gui.api.rating.change.model

import ru.astrainteractive.astrarating.core.settings.model.AstraRatingConfig

fun isRatingMessageLengthAllowed(message: String, config: AstraRatingConfig): Boolean {
    return message.length in config.minMessageLength..config.maxMessageLength
}
