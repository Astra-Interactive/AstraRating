@file:Suppress("FunctionNaming")

package ru.astrainteractive.astrarating.core.command

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import ru.astrainteractive.astralibs.command.api.argumenttype.IntArgumentConverter
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.astralibs.command.api.exception.BadArgumentException
import ru.astrainteractive.astralibs.command.api.exception.CommandException
import ru.astrainteractive.astralibs.command.api.exception.LocalizableComponentCommandException
import ru.astrainteractive.astralibs.command.api.exception.NoPlayerException
import ru.astrainteractive.astralibs.command.api.exception.NoPotionEffectTypeException
import ru.astrainteractive.astralibs.localization.component.LocalizableComponent
import ru.astrainteractive.astralibs.localization.text.LocalizedText
import ru.astrainteractive.astrarating.core.settings.AstraRatingPermission
import ru.astrainteractive.astrarating.core.settings.AstraRatingTranslation
import ru.astrainteractive.klibs.kstorage.api.asCachedMutableKrate
import ru.astrainteractive.klibs.kstorage.api.impl.DefaultMutableKrate
import kotlin.test.Test
import kotlin.test.assertEquals

class CommandExceptionHandlerTest {
    private val sender = RecordingConsoleKCommandSender()
    private val multiplatformCommand = MultiplatformCommand(FakeMultiplatformCommands(sender))
    private val translationKrate = DefaultMutableKrate(
        factory = ::AstraRatingTranslation,
        loader = { null }
    ).asCachedMutableKrate()
    private val handler = CommandExceptionHandler(
        multiplatformCommand = multiplatformCommand,
        translationKrate = translationKrate
    )
    private val commandError = AstraRatingTranslation().commandError

    private fun assertSenderReadOnly(message: LocalizableComponent) {
        assertEquals(listOf(message), sender.messages)
    }

    private fun execute(command: LiteralArgumentBuilder<Any>, input: String): Int {
        val dispatcher = CommandDispatcher<Any>()
        dispatcher.register(command)
        return dispatcher.execute(input, Any())
    }

    private fun executeFailing(failure: Throwable) {
        val command = with(multiplatformCommand) {
            command("arating") {
                runs(handler::handle) { _ -> throw failure }
            }
        }
        execute(command, "arating")
    }

    @Test
    fun GIVEN_sender_without_permission_WHEN_command_requires_it_THEN_sender_reads_no_permission() {
        val command = with(multiplatformCommand) {
            command("aratingreload") {
                runs(handler::handle) { ctx -> ctx.requirePermission(AstraRatingPermission.Reload) }
            }
        }

        execute(command, "aratingreload")

        assertSenderReadOnly(commandError.noPermission)
    }

    @Test
    fun GIVEN_console_WHEN_command_requires_player_THEN_console_reads_players_only() {
        val command = with(multiplatformCommand) {
            command("arating") {
                runs(handler::handle) { ctx -> ctx.requirePlayer() }
            }
        }

        execute(command, "arating")

        assertSenderReadOnly(commandError.playersOnly)
    }

    @Test
    fun GIVEN_player_who_never_joined_WHEN_command_looks_them_up_THEN_sender_reads_player_not_found() {
        executeFailing(NoPlayerException("Notch"))

        assertSenderReadOnly(commandError.playerNotFound)
    }

    @Test
    fun GIVEN_argument_that_is_not_a_number_WHEN_command_converts_it_THEN_sender_reads_invalid_argument() {
        val command = with(multiplatformCommand) {
            command("arating") {
                argument("page", StringArgumentType.string()) { pageArg ->
                    runs(handler::handle) { ctx -> ctx.requireArgument(pageArg, IntArgumentConverter) }
                }
            }
        }

        execute(command, "arating ten")

        assertSenderReadOnly(commandError.invalidArgument)
    }

    @Test
    fun GIVEN_argument_of_incompatible_type_WHEN_command_fails_THEN_sender_reads_invalid_argument() {
        executeFailing(BadArgumentException("ten", IntArgumentConverter))

        assertSenderReadOnly(commandError.invalidArgument)
    }

    @Test
    fun GIVEN_unknown_potion_effect_WHEN_command_fails_THEN_sender_reads_invalid_argument() {
        executeFailing(NoPotionEffectTypeException("flying"))

        assertSenderReadOnly(commandError.invalidArgument)
    }

    @Test
    fun GIVEN_command_exception_without_own_text_WHEN_command_fails_THEN_sender_reads_wrong_usage() {
        executeFailing(CommandException("Wrong argument usage"))

        assertSenderReadOnly(commandError.wrongUsage)
    }

    @Test
    fun GIVEN_failure_with_its_own_text_WHEN_command_fails_THEN_sender_reads_that_text() {
        val text = LocalizedText.shared("You have already rated this player today")

        executeFailing(LocalizableComponentCommandException(text))

        assertSenderReadOnly(text)
    }

    @Test
    fun GIVEN_argument_missing_on_the_node_WHEN_command_reads_it_THEN_sender_reads_unknown_error() {
        val missingArgument = MultiplatformCommand.BrigadierArgument(
            alias = "player",
            type = StringArgumentType.string(),
            clazz = String::class.java
        )
        val command = with(multiplatformCommand) {
            command("arating") {
                runs(handler::handle) { ctx -> ctx.requireArgument(missingArgument) }
            }
        }

        execute(command, "arating")

        assertSenderReadOnly(commandError.unknownError)
    }

    @Test
    fun GIVEN_translations_reloaded_WHEN_command_fails_THEN_sender_reads_reloaded_text() {
        val reloadedText = LocalizedText.shared("Reloaded unknown error")
        translationKrate.save(
            AstraRatingTranslation(commandError = AstraRatingTranslation.CommandError(unknownError = reloadedText))
        )

        executeFailing(IllegalStateException("Database is closed"))

        assertSenderReadOnly(reloadedText)
    }

    @Test
    fun GIVEN_sender_that_can_not_be_resolved_WHEN_command_fails_THEN_command_completes() {
        val unresolvedMultiplatformCommand = MultiplatformCommand(FakeMultiplatformCommands(sender = null))
        val unresolvedHandler = CommandExceptionHandler(
            multiplatformCommand = unresolvedMultiplatformCommand,
            translationKrate = translationKrate
        )
        val command = with(unresolvedMultiplatformCommand) {
            command("arating") {
                runs(unresolvedHandler::handle) { _ -> throw NoPlayerException("Notch") }
            }
        }

        val result = execute(command, "arating")

        assertEquals(Command.SINGLE_SUCCESS, result)
    }
}
