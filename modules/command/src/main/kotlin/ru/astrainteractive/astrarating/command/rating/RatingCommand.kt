package ru.astrainteractive.astrarating.command.rating

import ru.astrainteractive.astralibs.command.api.brigadier.sender.KCommandSender
import ru.astrainteractive.astralibs.server.player.KPlayer
import ru.astrainteractive.astralibs.server.player.OnlineKPlayer
import java.util.*

internal interface RatingCommand {
    sealed interface Result {
        class ChangeRating(
            val value: Int,
            val message: String,
            val executor: OnlineKPlayer,
            val ratedPlayer: KPlayer
        ) : Result

        class OpenPlayerRatingGui(
            val player: OnlineKPlayer,
            val selectedPlayerName: String,
            val selectedPlayerUUID: UUID
        ) : Result

        class OpenRatingsGui(val executor: OnlineKPlayer) : Result
        class Reload(val executor: KCommandSender) : Result
    }
}
