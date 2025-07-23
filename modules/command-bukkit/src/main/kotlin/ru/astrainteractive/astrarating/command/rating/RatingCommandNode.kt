package ru.astrainteractive.astrarating.command.rating

import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.command.api.argumenttype.OfflinePlayerArgument
import ru.astrainteractive.astralibs.command.api.argumenttype.StringArgumentType
import ru.astrainteractive.astralibs.command.api.util.command
import ru.astrainteractive.astralibs.command.api.util.findArgument
import ru.astrainteractive.astralibs.command.api.util.hints
import ru.astrainteractive.astralibs.command.api.util.runs
import ru.astrainteractive.astralibs.command.api.util.stringArgument
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astrarating.core.util.getValue
import ru.astrainteractive.klibs.kstorage.api.CachedKrate

@Suppress("LongMethod")
internal fun createRatingCommandNode(
    kyoriKrate: CachedKrate<KyoriComponentSerializer>,
    ratingCommandExecutor: RatingCommandExecutor,
): LiteralCommandNode<CommandSourceStack> {
    val kyori by kyoriKrate
    return with(kyori) {
        command("arating") {
            stringArgument("player") {
                hints(Bukkit.getOnlinePlayers().map(Player::getName))
                runs { ctx ->
                    val player = ctx.source.sender as? Player
                    if (player == null) {
                        // todo
                        return@runs
                    }
                    val targetPlayer = ctx.findArgument("player", OfflinePlayerArgument)
                    val targetPlayerUuid = targetPlayer?.uniqueId
                    val targetPlayerName = targetPlayer?.name
                    if (targetPlayerUuid == null || targetPlayerName == null) {
                        return@runs
                    }
                    ratingCommandExecutor.execute(
                        RatingCommand.Result.OpenPlayerRatingGui(
                            player = player,
                            selectedPlayerName = targetPlayerName,
                            selectedPlayerUUID = targetPlayerUuid
                        )
                    )
                }
            }
            stringArgument("like_dislike") {
                hints(listOf("like", "dislike", "+", "-"))
                stringArgument("player") {
                    hints(Bukkit.getOnlinePlayers().map(Player::getName))

                    runs { ctx ->
                        val executor = ctx.source.sender as? Player
                        if (executor == null) {
                            // todo
                            return@runs
                        }
                        val value = when (ctx.findArgument("like_dislike", StringArgumentType)) {
                            "like", "+" -> 1
                            "dislike", "-" -> -1
                            else -> {
                                // todo
                                return@runs
                            }
                        }
                        val ratedPlayer = ctx.findArgument("player", OfflinePlayerArgument)
                        if (ratedPlayer == null) {
                            // todo
                            return@runs
                        }
                        ratingCommandExecutor.execute(
                            RatingCommand.Result.ChangeRating(
                                value = value,
                                message = TODO(),
                                executor = executor,
                                ratedPlayer = ratedPlayer
                            )
                        )
                    }
                }
            }
            runs { ctx ->
                val executor = ctx.source.sender as? Player
                if (executor == null) {
                    // todo
                    return@runs
                }
                ratingCommandExecutor.execute(
                    RatingCommand.Result.OpenRatingsGui(
                        executor = executor
                    )
                )
            }
        }.build()
    }
}
