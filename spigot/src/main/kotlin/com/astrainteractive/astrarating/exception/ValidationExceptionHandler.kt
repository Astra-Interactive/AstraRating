package com.astrainteractive.astrarating.exception

import com.astrainteractive.astrarating.modules.ServiceLocator
import com.astrainteractive.astrarating.plugin.PluginTranslation
import ru.astrainteractive.astralibs.di.getValue

object ValidationExceptionHandler {
    private val translation: PluginTranslation by ServiceLocator.translation

    fun handle(e: Throwable) {
        if (e !is ValidationException) return
        when (e) {
            is ValidationException.DiscordNotLinked -> e.sender.sendMessage(translation.needDiscordLinked)
            is ValidationException.NoPermission -> e.sender.sendMessage(translation.noPermission)
            is ValidationException.NotEnoughOnServer -> e.sender.sendMessage(translation.notEnoughOnServer)
            is ValidationException.PlayerNotExists -> e.sender.sendMessage(translation.playerNotExists)
            is ValidationException.SamePlayer -> e.sender.sendMessage(translation.cantRateSelf)
            is ValidationException.OnlyPlayerCommand -> e.sender.sendMessage(translation.onlyPlayerCommand)
            is ValidationException.NotEnoughOnDiscord -> e.sender.sendMessage(translation.notEnoughOnDiscord)
            is ValidationException.AlreadyMaxDayVotes -> e.sender.sendMessage(translation.alreadyMaxDayVotes)
            is ValidationException.AlreadyMaxVotesOnPlayer -> e.sender.sendMessage(translation.alreadyMaxPlayerVotes)
            is ValidationException.DBException -> e.sender.sendMessage(translation.dbError)
            is ValidationException.WrongMessageLength -> e.sender.sendMessage(translation.wrongMessageLen)
        }
    }
}