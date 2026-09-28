package ru.astrainteractive.astrarating.core.command

import com.mojang.brigadier.context.CommandContext
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.astralibs.command.api.exception.ArgumentConverterException
import ru.astrainteractive.astralibs.command.api.exception.BadArgumentException
import ru.astrainteractive.astralibs.command.api.exception.LocalizableComponentCommandException
import ru.astrainteractive.astralibs.command.api.exception.NoPermissionException
import ru.astrainteractive.astralibs.command.api.exception.NoPlayerException
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

    fun handle(ctx: CommandContext<Any>, t: Throwable) {
        val message: LocalizableComponent = when (t) {
            is LocalizableComponentCommandException -> t.localizableComponent
            is BadArgumentException -> translation.commandError.wrongUsage
            is ArgumentConverterException -> translation.commandError.wrongUsage
            is NoPermissionException -> translation.commandError.noPermission
            is NoPlayerException -> translation.commandError.playerNotFound
            else -> {
                error(t) { "#handle unhandled exception ${t.message}" }
                translation.commandError.unknownError
            }
        }
        with(multiplatformCommand) {
            ctx.getSender().sendMessage(message)
        }
    }
}
