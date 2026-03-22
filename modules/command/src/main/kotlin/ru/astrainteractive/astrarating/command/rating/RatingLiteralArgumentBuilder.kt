package ru.astrainteractive.astrarating.command.rating

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import ru.astrainteractive.astralibs.command.api.argumenttype.KPlayerArgumentConverter
import ru.astrainteractive.astralibs.command.api.argumenttype.OnlineKPlayerArgumentConverter
import ru.astrainteractive.astralibs.command.api.argumenttype.StringArgumentConverter
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.astralibs.server.bridge.PlatformServer
import ru.astrainteractive.astralibs.server.player.KPlayer
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
    private fun onlinePlayerNames(): List<String> {
        return platformServer
            .getOnlinePlayers()
            .map(OnlineKPlayer::name)
    }

    private fun CommandContext<Any>.requireOnlinePlayer(
        arg: MultiplatformCommand.BrigadierArgument<String>
    ): OnlineKPlayer {
        val player = with(multiplatformCommand) {
            requireArgument(
                arg,
                OnlineKPlayerArgumentConverter(platformServer)
            )
        }
        if (!player.hasPlayedBefore()) throw UnknownPlayerCommandException()
        return player
    }

    private fun LiteralArgumentBuilder<Any>.playerCommand() {
        with(multiplatformCommand) {
            literal("player") {
                argument("player", StringArgumentType.string()) { playerArg ->
                    hints { onlinePlayerNames() }

                    runs(commandExceptionHandler::handle) { ctx ->
                        val executor = ctx.requirePlayer()
                        val target = ctx.requireOnlinePlayer(playerArg)

                        ratingCommandExecutor.execute(
                            RatingCommand.Result.OpenPlayerRatingGui(
                                player = executor,
                                selectedPlayerName = target.name,
                                selectedPlayerUUID = target.uuid
                            )
                        )
                    }
                }
            }
        }
    }

    private fun openRatingGui(
        ctx: CommandContext<Any>,
    ) {
        val playerCommandExecutor = with(multiplatformCommand) {
            ctx.requirePlayer()
        }
        val result = RatingCommand.Result.OpenRatingsGui(playerCommandExecutor)
        ratingCommandExecutor.execute(result)
    }

    private fun CommandContext<Any>.requireOfflinePlayer(arg: MultiplatformCommand.BrigadierArgument<String>): KPlayer {
        val player = with(multiplatformCommand) {
            requireArgument(
                arg,
                KPlayerArgumentConverter(platformServer)
            )
        }
        if (!player.hasPlayedBefore()) throw UnknownPlayerCommandException()
        return player
    }

    private fun CommandContext<Any>.parseRatingValue(arg: MultiplatformCommand.BrigadierArgument<String>): Int {
        val str = with(multiplatformCommand) {
            requireArgument(arg, StringArgumentConverter)
        }
        return when (str) {
            "like", "+" -> 1
            "dislike", "-" -> -1
            else -> throw UsageCommandException()
        }
    }

    private fun LiteralArgumentBuilder<Any>.ratingCommand() {
        with(multiplatformCommand) {
            literal("rating") {
                runs(commandExceptionHandler::handle) { ctx -> openRatingGui(ctx) }

                argument("like_dislike", StringArgumentType.string()) { ratingTypeArg ->
                    hints { listOf("like", "dislike", "+", "-") }

                    argument("player", StringArgumentType.string()) { playerArg ->
                        hints { onlinePlayerNames() }

                        argument("message", StringArgumentType.string()) { messageArg ->
                            hints { listOf("...") }

                            runs(commandExceptionHandler::handle) { ctx ->
                                val executor = ctx.requirePlayer()
                                val value = ctx.parseRatingValue(ratingTypeArg)
                                val ratedPlayer = ctx.requireOfflinePlayer(playerArg)

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
        }
    }

    fun create(): LiteralArgumentBuilder<Any> {
        return with(multiplatformCommand) {
            command("arating") {
                playerCommand()
                ratingCommand()
                runs(commandExceptionHandler::handle) { ctx -> openRatingGui(ctx) }
            }
        }
    }
}
