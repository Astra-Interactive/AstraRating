package ru.astrainteractive.astrarating.command.rating

import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.command.api.Command

interface RatingCommand : Command<RatingCommand.Result, RatingCommand.Result> {
    sealed interface Result {
        data object WrongUsage : Result
        class ChangeRating(
            val value: Int,
            val message: String,
            val executor: Player,
            val ratedPlayer: OfflinePlayer
        ) : Result

        class OpenPlayerRatingGui(val player: Player, val selectedPlayerName: String) : Result
        class OpenRatingsGui(val executor: Player) : Result
        class Reload(val executor: CommandSender) : Result
        data object NotPlayer : Result
    }
}
