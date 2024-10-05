package ru.astrainteractive.astrarating.command.rating

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.command.api.context.BukkitCommandContext
import ru.astrainteractive.astralibs.command.api.parser.CommandParser

internal class RatingCommandParser : CommandParser<RatingCommand.Result, BukkitCommandContext> {

    @Suppress("ThrowsCount")
    private fun parseChangeRating(
        label: String,
        args: Array<out String>,
        sender: CommandSender
    ): RatingCommand.Result {
        val amount = when (label) {
            "like", "+" -> 1
            "dislike", "-" -> -1
            else -> throw RatingCommand.Error.WrongUsage
        }

        val commandExecutor = (sender as? Player) ?: throw RatingCommand.Error.NotPlayer

        val ratedPlayer = args.getOrNull(1)
            ?.let(Bukkit::getOfflinePlayer) ?: throw RatingCommand.Error.WrongUsage

        val message = args.toList().subList(2, args.size).joinToString(" ")
        return RatingCommand.Result.ChangeRating(
            value = amount,
            message = message,
            executor = commandExecutor,
            ratedPlayer = ratedPlayer
        )
    }

    override fun parse(commandContext: BukkitCommandContext): RatingCommand.Result {
        val sender = commandContext.sender
        return when (val label = commandContext.args.getOrNull(0)) {
            "like", "+", "dislike", "-" -> {
                parseChangeRating(
                    label = label,
                    args = commandContext.args,
                    sender = commandContext.sender
                )
            }

            "rating" -> {
                if (sender !is Player) throw RatingCommand.Error.NotPlayer
                commandContext.args.getOrNull(1)?.takeIf(String::isNotBlank)?.let { requestedPlayerName ->
                    val requestedPlayerUuid = Bukkit.getOfflinePlayer(requestedPlayerName).uniqueId
                    RatingCommand.Result.OpenPlayerRatingGui(
                        player = sender,
                        selectedPlayerName = requestedPlayerName,
                        selectedPlayerUUID = requestedPlayerUuid
                    )
                } ?: RatingCommand.Result.OpenRatingsGui(sender)
            }

            "reload" -> RatingCommand.Result.Reload(sender)
            else -> throw RatingCommand.Error.WrongUsage
        }
    }
}
