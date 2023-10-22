package ru.astrainteractive.astrarating.command.rating.exception

import net.kyori.adventure.text.Component
import ru.astrainteractive.astralibs.serialization.KyoriComponentSerializer
import ru.astrainteractive.astrarating.model.PluginTranslation

class ValidationExceptionHandler(
    private val translation: PluginTranslation,
    private val translationContext: KyoriComponentSerializer
) {
    private fun String.translationComponent(): Component {
        return translationContext.toComponent(this)
    }

    fun handle(e: Throwable) {
        if (e !is ValidationException) return
        when (e) {
            is ValidationException.DiscordNotLinked -> e.sender.sendMessage(
                translation.needDiscordLinked.translationComponent()
            )
            is ValidationException.NoPermission -> e.sender.sendMessage(translation.noPermission.translationComponent())
            is ValidationException.NotEnoughOnServer -> e.sender.sendMessage(
                translation.notEnoughOnServer.translationComponent()
            )
            is ValidationException.PlayerNotExists -> e.sender.sendMessage(
                translation.playerNotExists.translationComponent()
            )
            is ValidationException.SamePlayer -> e.sender.sendMessage(translation.cantRateSelf.translationComponent())
            is ValidationException.OnlyPlayerCommand -> e.sender.sendMessage(
                translation.onlyPlayerCommand.translationComponent()
            )
            is ValidationException.NotEnoughOnDiscord -> e.sender.sendMessage(
                translation.notEnoughOnDiscord.translationComponent()
            )
            is ValidationException.AlreadyMaxDayVotes -> e.sender.sendMessage(
                translation.alreadyMaxDayVotes.translationComponent()
            )
            is ValidationException.AlreadyMaxVotesOnPlayer -> e.sender.sendMessage(
                translation.alreadyMaxPlayerVotes.translationComponent()
            )
            is ValidationException.DBException -> e.sender.sendMessage(translation.dbError.translationComponent())
            is ValidationException.WrongMessageLength -> e.sender.sendMessage(
                translation.wrongMessageLen.translationComponent()
            )
        }
    }
}
