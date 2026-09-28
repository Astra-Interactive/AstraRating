@file:Suppress("FunctionNaming")

package ru.astrainteractive.astrarating.command.reload

import com.mojang.brigadier.CommandDispatcher
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.localization.component.LocalizableComponent
import ru.astrainteractive.astralibs.server.permission.Permission
import ru.astrainteractive.astrarating.core.command.CommandExceptionHandler
import ru.astrainteractive.astrarating.core.command.FakeMultiplatformCommands
import ru.astrainteractive.astrarating.core.command.RecordingConsoleKCommandSender
import ru.astrainteractive.astrarating.core.settings.AstraRatingPermission
import ru.astrainteractive.astrarating.core.settings.AstraRatingTranslation
import ru.astrainteractive.klibs.kstorage.api.asCachedMutableKrate
import ru.astrainteractive.klibs.kstorage.api.impl.DefaultMutableKrate
import kotlin.test.Test
import kotlin.test.assertEquals

class ReloadLiteralArgumentBuilderTest {
    private val translation = AstraRatingTranslation()
    private var reloadCount = 0
    private val plugin = Lifecycle.Lambda(onReload = { reloadCount++ })

    private fun executeReload(grantedPermissions: Set<Permission>): List<LocalizableComponent> {
        val sender = RecordingConsoleKCommandSender(grantedPermissions = grantedPermissions)
        val multiplatformCommand = MultiplatformCommand(FakeMultiplatformCommands(sender))
        val translationKrate = DefaultMutableKrate(
            factory = { translation },
            loader = { null }
        ).asCachedMutableKrate()
        val command = ReloadLiteralArgumentBuilder(
            lifecyclePlugin = plugin,
            multiplatformCommand = multiplatformCommand,
            commandExceptionHandler = CommandExceptionHandler(
                multiplatformCommand = multiplatformCommand,
                translationKrate = translationKrate
            ),
            translationKrate = translationKrate
        ).create()
        val dispatcher = CommandDispatcher<Any>()
        dispatcher.register(command)
        dispatcher.execute("aratingreload", Any())
        return sender.messages
    }

    @Test
    fun GIVEN_sender_without_reload_permission_WHEN_reload_runs_THEN_plugin_is_not_reloaded() {
        val messages = executeReload(grantedPermissions = emptySet())

        assertEquals(listOf<LocalizableComponent>(translation.commandError.noPermission), messages)
        assertEquals(0, reloadCount)
    }

    @Test
    fun GIVEN_sender_with_reload_permission_WHEN_reload_runs_THEN_plugin_is_reloaded_once() {
        val messages = executeReload(grantedPermissions = setOf(AstraRatingPermission.Reload))

        assertEquals(listOf<LocalizableComponent>(translation.reload.started, translation.reload.completed), messages)
        assertEquals(1, reloadCount)
    }
}
