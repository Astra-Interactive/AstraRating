package ru.astrainteractive.astrarating.command.rating

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import ru.astrainteractive.astralibs.command.api.argumenttype.KPlayerArgumentConverter
import ru.astrainteractive.astralibs.command.api.argumenttype.OnlineKPlayerArgumentConverter
import ru.astrainteractive.astralibs.command.api.argumenttype.StringArgumentConverter
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.astralibs.server.bridge.PlatformServer
import ru.astrainteractive.astralibs.server.player.OnlineKPlayer
import ru.astrainteractive.astrarating.command.exception.CommandExceptionHandler
import ru.astrainteractive.astrarating.command.exception.UnknownPlayerCommandException
import ru.astrainteractive.astrarating.command.exception.UsageCommandException

internal class RatingLiteralArgumentBuilder(
    private val commandExceptionHandler: CommandExceptionHandler,
    private val multiplatformCommand: MultiplatformCommand,
    private val platformServer: PlatformServer,
    private val ratingCommandExecutor: RatingCommandExecutor,
) {
    private fun openRating(
        ctx: CommandContext<Any>,
    ) {
        val executor = with(multiplatformCommand) {
            ctx.requirePlayer()
        }
        ratingCommandExecutor.execute(
            RatingCommand.Result.OpenRatingsGui(
                executor = executor
            )
        )
    }

    @Suppress("LongMethod")
    fun create(): LiteralArgumentBuilder<Any> {
        return with(multiplatformCommand) {
            command("arating") {
                literal("player") {
                    argument("player", StringArgumentType.string()) { playerArg ->
                        hints { platformServer.getOnlinePlayers().map(OnlineKPlayer::name) }
                        runs { ctx ->
                            val player = ctx.requirePlayer()
                            val targetPlayer = runCatching {
                                ctx.requireArgument(playerArg, OnlineKPlayerArgumentConverter(platformServer))
                            }.getOrNull()
                            val targetPlayerUuid = targetPlayer?.uuid
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
                        openRating(ctx)
                    }
                    argument("like_dislike", StringArgumentType.string()) { ratingTypeArg ->
                        hints { listOf("like", "dislike", "+", "-") }
                        argument("player", StringArgumentType.string()) player@{ playerArg ->
                            hints { platformServer.getOnlinePlayers().map(OnlineKPlayer::name) }
                            argument("message", StringArgumentType.string()) message@{ messageArg ->
                                hints { listOf("...") }
                                runs { ctx ->
                                    val executor = ctx.requirePlayer()
                                    val value = when (ctx.requireArgument(ratingTypeArg, StringArgumentConverter)) {
                                        "like", "+" -> 1
                                        "dislike", "-" -> -1
                                        else -> {
                                            commandExceptionHandler.handle(ctx, UsageCommandException())
                                            return@runs
                                        }
                                    }
                                    val ratedPlayer = runCatching {
                                        ctx.requireArgument(playerArg, KPlayerArgumentConverter(platformServer))
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
                runs { ctx -> openRating(ctx) }
            }
        }
    }
}
