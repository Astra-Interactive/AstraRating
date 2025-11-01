package ru.astrainteractive.astrarating.command.rating

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.command.api.argumenttype.OfflinePlayerArgument
import ru.astrainteractive.astralibs.command.api.argumenttype.StringArgumentConverter
import ru.astrainteractive.astralibs.command.api.util.argument
import ru.astrainteractive.astralibs.command.api.util.command
import ru.astrainteractive.astralibs.command.api.util.hints
import ru.astrainteractive.astralibs.command.api.util.literal
import ru.astrainteractive.astralibs.command.api.util.requireArgument
import ru.astrainteractive.astralibs.command.api.util.requirePlayer
import ru.astrainteractive.astralibs.command.api.util.runs
import ru.astrainteractive.astrarating.command.exception.CommandExceptionHandler
import ru.astrainteractive.astrarating.command.exception.OnlyPlayerCommandException
import ru.astrainteractive.astrarating.command.exception.UnknownPlayerCommandException
import ru.astrainteractive.astrarating.command.exception.UsageCommandException

private fun openRating(
    ctx: CommandContext<CommandSourceStack>,
    commandExceptionHandler: CommandExceptionHandler,
    ratingCommandExecutor: RatingCommandExecutor
) {
    val executor = ctx.source.sender as? Player
    if (executor == null) {
        commandExceptionHandler.handle(ctx, OnlyPlayerCommandException())
        return
    }
    ratingCommandExecutor.execute(
        RatingCommand.Result.OpenRatingsGui(
            executor = executor
        )
    )
}

@Suppress("LongMethod")
internal fun createRatingCommandNode(
    ratingCommandExecutor: RatingCommandExecutor,
    commandExceptionHandler: CommandExceptionHandler
): LiteralCommandNode<CommandSourceStack> {
    return command("arating") {
        literal("player") {
            argument("player", StringArgumentType.string()) { playerArg ->
                hints { Bukkit.getOnlinePlayers().map(Player::getName) }
                runs { ctx ->

                    val player = ctx.requirePlayer()
                    val targetPlayer = runCatching {
                        ctx.requireArgument(playerArg, OfflinePlayerArgument)
                    }.getOrNull()
                    val targetPlayerUuid = targetPlayer?.uniqueId
                    val targetPlayerName = targetPlayer?.name
                    if (targetPlayerUuid == null || targetPlayerName == null) {
                        commandExceptionHandler.handle(ctx, UnknownPlayerCommandException())
                        return@runs
                    }
                    if (!targetPlayer.hasPlayedBefore()) {
                        commandExceptionHandler.handle(ctx, UnknownPlayerCommandException())
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
        }
        literal("rating") {
            runs { ctx ->
                openRating(
                    ctx = ctx,
                    commandExceptionHandler = commandExceptionHandler,
                    ratingCommandExecutor = ratingCommandExecutor
                )
            }
            argument("like_dislike", StringArgumentType.string()) { ratingTypeArg ->
                hints { listOf("like", "dislike", "+", "-") }
                argument("player", StringArgumentType.string()) player@{ playerArg ->
                    hints { Bukkit.getOnlinePlayers().map(Player::getName) }
                    argument("message", StringArgumentType.greedyString()) message@{ messageArg ->
                        hints { listOf("...") }
                        runs { ctx ->
                            val executor = ctx.source.sender as? Player
                            if (executor == null) {
                                commandExceptionHandler.handle(ctx, OnlyPlayerCommandException())
                                return@runs
                            }
                            val value = when (ctx.requireArgument(ratingTypeArg, StringArgumentConverter)) {
                                "like", "+" -> 1
                                "dislike", "-" -> -1
                                else -> {
                                    commandExceptionHandler.handle(ctx, UsageCommandException())
                                    return@runs
                                }
                            }
                            val ratedPlayer = runCatching {
                                ctx.requireArgument(playerArg, OfflinePlayerArgument)
                            }.getOrNull()
                            if (ratedPlayer == null) {
                                commandExceptionHandler.handle(ctx, UnknownPlayerCommandException())
                                return@runs
                            }
                            if (!ratedPlayer.hasPlayedBefore()) {
                                commandExceptionHandler.handle(ctx, UnknownPlayerCommandException())
                                return@runs
                            }
                            ratingCommandExecutor.execute(
                                RatingCommand.Result.ChangeRating(
                                    value = value,
                                    message = ctx.requireArgument(messageArg, StringArgumentConverter),
                                    executor = executor,
                                    ratedPlayer = ratedPlayer
                                )
                            )
                        }
                    }
                }
            }
        }
        runs { ctx ->
            openRating(
                ctx = ctx,
                commandExceptionHandler = commandExceptionHandler,
                ratingCommandExecutor = ratingCommandExecutor
            )
        }
    }.build()
}
