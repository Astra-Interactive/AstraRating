package ru.astrainteractive.astrarating.event.kill.model

internal sealed interface LowerKillerRatingResult {
    data object Disabled : LowerKillerRatingResult
    data object KilledPlayerNotRated : LowerKillerRatingResult
    data object StorageFailed : LowerKillerRatingResult
    data object Lowered : LowerKillerRatingResult
}
