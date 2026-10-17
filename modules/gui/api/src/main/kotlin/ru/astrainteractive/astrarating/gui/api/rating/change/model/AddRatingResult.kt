package ru.astrainteractive.astrarating.gui.api.rating.change.model

sealed interface AddRatingResult {
    data object NoPermission : AddRatingResult
    data object PlayerNotExists : AddRatingResult
    data object SamePlayer : AddRatingResult
    data object AlreadyMaxDayVotes : AddRatingResult
    data object AlreadyMaxVotesOnPlayer : AddRatingResult
    data object MessageNotCorrect : AddRatingResult
    data object StorageFailed : AddRatingResult
    data object Success : AddRatingResult
}
