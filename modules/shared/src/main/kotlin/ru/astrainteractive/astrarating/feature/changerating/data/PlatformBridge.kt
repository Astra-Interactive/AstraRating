package ru.astrainteractive.astrarating.feature.changerating.data

import ru.astrainteractive.astrarating.model.PlayerModel

interface PlatformBridge {

    fun isPlayerExists(playerModel: PlayerModel?): Boolean

    fun hasEnoughTime(playerModel: PlayerModel): Boolean
}
