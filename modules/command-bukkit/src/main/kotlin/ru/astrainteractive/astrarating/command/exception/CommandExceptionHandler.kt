package ru.astrainteractive.astrarating.command.exception

import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import ru.astrainteractive.astralibs.command.api.exception.ArgumentTypeException
import ru.astrainteractive.astralibs.command.api.exception.BadArgumentException
import ru.astrainteractive.astralibs.command.api.exception.NoPermissionException
import ru.astrainteractive.astralibs.command.api.exception.NoPlayerException
import ru.astrainteractive.astralibs.command.api.exception.StringDescCommandException
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.kyori.unwrap
import ru.astrainteractive.astralibs.logging.JUtiltLogger
import ru.astrainteractive.astralibs.logging.Logger
import ru.astrainteractive.astrarating.core.settings.AstraRatingTranslation
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.util.getValue

class CommandExceptionHandler(
    translationKrate: CachedKrate<AstraRatingTranslation>,
    kyoriKrate: CachedKrate<KyoriComponentSerializer>
) : KyoriComponentSerializer by kyoriKrate.unwrap(),
    Logger by JUtiltLogger("AstraRating-CommandExceptionHandler") {
    private val translation by translationKrate

    fun handle(ctx: CommandContext<CommandSourceStack>, t: Throwable) {
        val desc = when (t) {
            is StringDescCommandException -> t.stringDesc
            is BadArgumentException -> translation.wrongUsage
            is ArgumentTypeException -> translation.wrongUsage
            is NoPermissionException -> translation.noPermission
            is UnknownPlayerCommandException,
            is NoPlayerException -> translation.playerNotExists
            is OnlyPlayerCommandException -> translation.onlyPlayerCommand
            else -> {
                error(t) { "#handle unhandled exception ${t.message}" }
                translation.unknownError
            }
        }
        ctx.source.sender.sendMessage(desc.component)
    }
}
