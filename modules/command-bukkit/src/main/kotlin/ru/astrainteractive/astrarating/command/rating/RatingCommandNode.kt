package ru.astrainteractive.astrarating.command.rating

import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.command.api.argumenttype.OfflinePlayerArgument
import ru.astrainteractive.astralibs.command.api.argumenttype.StringArgumentType
import ru.astrainteractive.astralibs.command.api.util.command
import ru.astrainteractive.astralibs.command.api.util.findArgument
import ru.astrainteractive.astralibs.command.api.util.hints
import ru.astrainteractive.astralibs.command.api.util.literal
import ru.astrainteractive.astralibs.command.api.util.runs
import ru.astrainteractive.astralibs.command.api.util.stringArgument
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astrarating.command.exception.CommandExceptionHandler
import ru.astrainteractive.astrarating.command.exception.OnlyPlayerCommandException
import ru.astrainteractive.astrarating.command.exception.UnknownPlayerCommandException
import ru.astrainteractive.astrarating.command.exception.UsageCommandException
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.util.getValue

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
    kyoriKrate: CachedKrate<KyoriComponentSerializer>,
    ratingCommandExecutor: RatingCommandExecutor,
    commandExceptionHandler: CommandExceptionHandler
): LiteralCommandNode<CommandSourceStack> {
    val kyori by kyoriKrate
    return with(kyori) {
        command("arating") {
            literal("player") {
                stringArgument("player") {
                    hints(Bukkit.getOnlinePlayers().map(Player::getName))
                    runs { ctx ->
                        val player = ctx.source.sender as? Player
                        if (player == null) {
                            commandExceptionHandler.handle(ctx, OnlyPlayerCommandException())
                            return@runs
                        }
                        val targetPlayer = ctx.findArgument("player", OfflinePlayerArgument)
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
                stringArgument("like_dislike") {
                    hints(listOf("like", "dislike", "+", "-"))
                    stringArgument("player") player@{
                        hints { Bukkit.getOnlinePlayers().map(Player::getName) }
                        fun RequiredArgumentBuilder<CommandSourceStack, *>.messageArgument(
                            sizeLeft: Int,
                            maxSize: Int
                        ) {
                            if (sizeLeft < 0) return
                            stringArgument("message_$sizeLeft") message@{
                                hints(listOf("..."))
                                messageArgument(sizeLeft - 1, maxSize)
                                runs { ctx ->
                                    val executor = ctx.source.sender as? Player
                                    if (executor == null) {
                                        commandExceptionHandler.handle(ctx, OnlyPlayerCommandException())
                                        return@runs
                                    }
                                    val value = when (ctx.findArgument("like_dislike", StringArgumentType)) {
                                        "like", "+" -> 1
                                        "dislike", "-" -> -1
                                        else -> {
                                            commandExceptionHandler.handle(ctx, UsageCommandException())
                                            return@runs
                                        }
                                    }
                                    val ratedPlayer = ctx.findArgument("player", OfflinePlayerArgument)
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
                                            message = (sizeLeft..maxSize)
                                                .reversed()
                                                .mapNotNull { i -> ctx.findArgument("message_$i", StringArgumentType) }
                                                .joinToString(" "),
                                            executor = executor,
                                            ratedPlayer = ratedPlayer
                                        )
                                    )
                                }
                            }
                        }

                        messageArgument(300, 300)
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
}
