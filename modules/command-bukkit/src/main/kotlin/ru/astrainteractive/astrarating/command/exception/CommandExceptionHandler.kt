package ru.astrainteractive.astrarating.command.exception

import com.mojang.brigadier.context.CommandContext
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.astralibs.command.api.exception.ArgumentConverterException
import ru.astrainteractive.astralibs.command.api.exception.BadArgumentException
import ru.astrainteractive.astralibs.command.api.exception.NoPermissionException
import ru.astrainteractive.astralibs.command.api.exception.NoPlayerException
import ru.astrainteractive.astralibs.command.api.exception.StringDescCommandException
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.kyori.unwrap
import ru.astrainteractive.astralibs.server.KAudience
import ru.astrainteractive.astrarating.core.settings.AstraRatingTranslation
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.util.getValue
import ru.astrainteractive.klibs.mikro.core.logging.JUtiltLogger
import ru.astrainteractive.klibs.mikro.core.logging.Logger
import ru.astrainteractive.klibs.mikro.core.util.tryCast

class CommandExceptionHandler(
    translationKrate: CachedKrate<AstraRatingTranslation>,
    kyoriKrate: CachedKrate<KyoriComponentSerializer>,
    private val multiplatformCommand: MultiplatformCommand<*>,
) : KyoriComponentSerializer by kyoriKrate.unwrap(),
    Logger by JUtiltLogger("AstraRating-CommandExceptionHandler") {
    private val translation by translationKrate

    fun handle(ctx: CommandContext<*>, t: Throwable) {
        val desc = when (t) {
            is StringDescCommandException -> t.stringDesc
            is BadArgumentException -> translation.general.wrongUsage
            is ArgumentConverterException -> translation.general.wrongUsage
            is NoPermissionException -> translation.general.noPermission
            is UnknownPlayerCommandException,
            is NoPlayerException -> translation.general.playerNotExist

            is OnlyPlayerCommandException -> translation.general.onlyPlayerCommand
            else -> {
                error(t) { "#handle unhandled exception ${t.message}" }
                translation.general.unknownError
            }
        }
        multiplatformCommand.commands
            .getSender(ctx)
            .tryCast<KAudience>()
            ?.sendMessage(desc.component)
    }
}
