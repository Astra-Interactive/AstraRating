package ru.astrainteractive.astrarating.command.rating

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.command.api.CommandParser

class RatingCommandParser : CommandParser<RatingCommand.Result> {

    private fun parseChangeRating(
        label: String,
        args: Array<out String>,
        sender: CommandSender
    ): RatingCommand.Result {
        val amount = when (label) {
            "like", "+" -> 1
            "dislike", "-" -> -1
            else -> return RatingCommand.Result.WrongUsage
        }

        val commandExecutor = (sender as? Player) ?: return RatingCommand.Result.NotPlayer

        val ratedPlayer = args.getOrNull(1)
            ?.let(Bukkit::getOfflinePlayer) ?: return RatingCommand.Result.WrongUsage

        val message = args.toList().subList(2, args.size).joinToString(" ")
        return RatingCommand.Result.ChangeRating(
            value = amount,
            message = message,
            executor = commandExecutor,
            ratedPlayer = ratedPlayer
        )
    }

    override fun parse(args: Array<out String>, sender: CommandSender): RatingCommand.Result {
        return when (val label = args.getOrNull(0)) {
            "like", "+", "dislike", "-" -> {
                parseChangeRating(
                    label = label,
                    args = args,
                    sender = sender
                )
            }

            "rating" -> {
                if (sender !is Player) return RatingCommand.Result.NotPlayer
                args.getOrNull(1)?.takeIf(String::isNotBlank)?.let { requestedPlayer ->
                    RatingCommand.Result.OpenPlayerRatingGui(sender, requestedPlayer.uppercase())
                } ?: RatingCommand.Result.OpenRatingsGui(sender)
            }

            "reload" -> RatingCommand.Result.Reload(sender)
            else -> RatingCommand.Result.WrongUsage
        }
    }
}
