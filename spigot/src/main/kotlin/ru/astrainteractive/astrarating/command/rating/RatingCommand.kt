package ru.astrainteractive.astrarating.command.rating

import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.command.api.Command

interface RatingCommand : Command<RatingCommand.Result, RatingCommand.Input> {
    sealed interface Input {
        class ChangeRating(
            val value: Int,
            val message: String,
            val executor: Player,
            val rated: OfflinePlayer
        ) : Input

        class Reload(val executor: CommandSender) : Input
        class OpenRatingGui(val player: Player) : Input
    }

    sealed interface Result {
        data object WrongUsage : Result
        class ChangeRating(
            val value: Int,
            val message: String,
            val executor: Player,
            val ratedPlayer: OfflinePlayer
        ) : Result

        class Rating(val executor: Player) : Result
        class Reload(val executor: CommandSender) : Result
        data object NotPlayer : Result
    }
}
