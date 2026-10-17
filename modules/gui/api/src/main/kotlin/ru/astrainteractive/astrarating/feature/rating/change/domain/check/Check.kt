package ru.astrainteractive.astrarating.feature.rating.change.domain.check

import ru.astrainteractive.astrarating.feature.rating.change.model.RatingPlayer

internal sealed interface Check {
    class CanVoteOnPlayer(val creator: RatingPlayer, val rated: RatingPlayer?) : Check
    class CanVoteToday(val playerModel: RatingPlayer) : Check
    class CanVote(val playerModel: RatingPlayer) : Check
    class EnoughTime(val playerModel: RatingPlayer) : Check
    class PlayerExists(val playerModel: RatingPlayer) : Check
    class MessageCorrect(val message: String) : Check
    class NotSamePlayer(val first: RatingPlayer, val second: RatingPlayer) : Check
}
