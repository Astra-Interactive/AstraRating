package ru.astrainteractive.astrarating.core.command

import com.mojang.brigadier.context.CommandContext
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.astralibs.command.api.exception.ArgumentConverterException
import ru.astrainteractive.astralibs.command.api.exception.BadArgumentException
import ru.astrainteractive.astralibs.command.api.exception.CommandException
import ru.astrainteractive.astralibs.command.api.exception.LocalizableComponentCommandException
import ru.astrainteractive.astralibs.command.api.exception.NoPermissionException
import ru.astrainteractive.astralibs.command.api.exception.NoPlayerException
import ru.astrainteractive.astralibs.command.api.exception.NoPotionEffectTypeException
import ru.astrainteractive.astralibs.command.api.exception.NotPlayerExecutorException
import ru.astrainteractive.astralibs.localization.component.LocalizableComponent
import ru.astrainteractive.astrarating.core.settings.AstraRatingTranslation
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.getValue
import ru.astrainteractive.klibs.mikro.core.logging.JUtiltLogger
import ru.astrainteractive.klibs.mikro.core.logging.Logger

class CommandExceptionHandler(
    private val multiplatformCommand: MultiplatformCommand,
    translationKrate: CachedKrate<AstraRatingTranslation>,
) : Logger by JUtiltLogger("AstraRating-CommandExceptionHandler") {
    private val translation by translationKrate

    private fun messageOf(throwable: Throwable, commandName: String): LocalizableComponent = when (throwable) {
        is LocalizableComponentCommandException -> throwable.localizableComponent
        is NoPermissionException -> translation.commandError.noPermission
        is NotPlayerExecutorException -> translation.commandError.playersOnly
        is NoPlayerException -> translation.commandError.playerNotFound
        is ArgumentConverterException,
        is BadArgumentException,
        is NoPotionEffectTypeException -> translation.commandError.invalidArgument
        is CommandException -> translation.commandError.wrongUsage
        else -> {
            error(throwable) { "#messageOf /$commandName failed with an unexpected exception" }
            translation.commandError.unknownError
        }
    }

    fun handle(ctx: CommandContext<Any>, throwable: Throwable) {
        val commandName = ctx.input.substringBefore(' ')
        val sender = runCatching { with(multiplatformCommand) { ctx.getSender() } }
            .getOrElse { senderError ->
                error(throwable) {
                    "#handle /$commandName failed and its sender could not be resolved: ${senderError.message}"
                }
                return
            }
        sender.sendMessage(messageOf(throwable, commandName))
    }
}
