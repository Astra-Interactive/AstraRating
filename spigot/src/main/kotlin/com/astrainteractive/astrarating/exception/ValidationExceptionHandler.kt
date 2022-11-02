package com.astrainteractive.astrarating.exception

import com.astrainteractive.astrarating.modules.TranslationProvider
import com.astrainteractive.astrarating.utils.PluginTranslation

object ValidationExceptionHandler : ISealedExceptionHandler<ValidationException> {
    override val clazz: Class<ValidationException> = ValidationException::class.java
    private val translation: PluginTranslation
        get() = TranslationProvider.value

    override fun handle(e: ValidationException) {
        when (e) {
            is ValidationException.DiscordNotLinked -> e.sender.sendMessage(translation.needDiscordLinked)
            is ValidationException.NoPermission -> e.sender.sendMessage(translation.noPermission)
            is ValidationException.NotEnoughOnServer -> e.sender.sendMessage(translation.notEnoughOnServer)
            is ValidationException.PlayerNotExists -> e.sender.sendMessage(translation.playerNotExists)
            is ValidationException.SamePlayer -> e.sender.sendMessage(translation.cantRateSelf)
            is ValidationException.OnlyPlayerCommand -> e.sender.sendMessage(translation.onlyPlayerCommand)
            is ValidationException.NotEnoughOnDiscord -> e.sender.sendMessage(translation.notEnoughOnDiscord)
        }
    }
}