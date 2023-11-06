package ru.astrainteractive.astrarating.command.rating

import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.command.api.CommandParser

class RatingCommandParser(override val alias: String) : CommandParser<RatingCommandParser.Result> {
    sealed interface Result {
        data object WrongUsage : Result
        class ChangeRating(
            val value: Int,
            val message: String,
            val executor: Player,
            val ratedPlayer: OfflinePlayer
        ) : Result

        class Rating(val executor: CommandSender) : Result
        class Reload(val executor: CommandSender) : Result
        data object NotPlayer : Result
    }

    private fun parseChangeRating(
        label: String,
        args: Array<out String>,
        sender: CommandSender
    ): Result {
        val amount = when (label) {
            "like", "+" -> 1
            "dislike", "-" -> -1
            else -> return Result.WrongUsage
        }

        val commandExecutor = (sender as? Player) ?: return Result.NotPlayer

        val ratedPlayer = args.getOrNull(1)
            ?.let(Bukkit::getOfflinePlayer) ?: return Result.WrongUsage

        val message = args.toList().subList(2, args.size).joinToString(" ")
        return Result.ChangeRating(
            value = amount,
            message = message,
            executor = commandExecutor,
            ratedPlayer = ratedPlayer
        )
    }

    override fun parse(args: Array<out String>, sender: CommandSender): Result {
        return when (val label = args.getOrNull(0)) {
            "like", "+", "dislike", "-" -> {
                parseChangeRating(
                    label = label,
                    args = args,
                    sender = sender
                )
            }

            "rating" -> Result.Rating(sender)
            "reload" -> Result.Rating(sender)
            else -> Result.WrongUsage
        }
    }
}
