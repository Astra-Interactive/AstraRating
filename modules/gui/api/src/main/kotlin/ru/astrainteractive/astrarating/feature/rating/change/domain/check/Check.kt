package ru.astrainteractive.astrarating.feature.rating.change.domain.check

import ru.astrainteractive.astrarating.data.exposed.model.PlayerModel

internal sealed interface Check {
    class CanVoteOnPlayer(val creator: PlayerModel, val rated: PlayerModel?) : Check
    class CanVoteToday(val playerModel: PlayerModel) : Check
    class CanVote(val playerModel: PlayerModel) : Check
    class EnoughTime(val playerModel: PlayerModel) : Check
    class PlayerExists(val playerModel: PlayerModel) : Check
    class MessageCorrect(val message: String) : Check
    class NotSamePlayer(val first: PlayerModel, val second: PlayerModel) : Check
}
