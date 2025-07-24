package ru.astrainteractive.astrarating.command.rating

import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.UUID

internal interface RatingCommand {
    sealed interface Result {
        class ChangeRating(
            val value: Int,
            val message: String,
            val executor: Player,
            val ratedPlayer: OfflinePlayer
        ) : Result

        class OpenPlayerRatingGui(
            val player: Player,
            val selectedPlayerName: String,
            val selectedPlayerUUID: UUID
        ) : Result

        class OpenRatingsGui(val executor: Player) : Result
        class Reload(val executor: CommandSender) : Result
    }
}
