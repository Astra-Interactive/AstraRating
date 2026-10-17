package ru.astrainteractive.astrarating.event.kill.util

import ru.astrainteractive.astrarating.core.settings.model.AstraRatingConfig

internal val AstraRatingConfig.Events.Event.changesRating: Boolean
    get() = enabled && changeBy != 0

internal fun AstraRatingConfig.Events.Event.isKillPenalized(killedPlayerRating: Int): Boolean {
    return changesRating && killedPlayerRating > 0
}
