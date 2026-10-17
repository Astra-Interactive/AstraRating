package ru.astrainteractive.astrarating.command.rating.model

import ru.astrainteractive.astralibs.server.player.KPlayer
import ru.astrainteractive.astralibs.server.player.OnlineKPlayer
import java.util.UUID

internal sealed interface RatingCommand {
    class ChangeRating(
        val value: Int,
        val message: String,
        val executor: OnlineKPlayer,
        val ratedPlayer: KPlayer
    ) : RatingCommand

    class OpenPlayerRatingGui(
        val player: OnlineKPlayer,
        val selectedPlayerName: String,
        val selectedPlayerUUID: UUID
    ) : RatingCommand

    class OpenRatingsGui(val executor: OnlineKPlayer) : RatingCommand
}
