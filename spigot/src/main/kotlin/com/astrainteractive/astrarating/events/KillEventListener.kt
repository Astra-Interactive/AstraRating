package com.astrainteractive.astrarating.events

import com.astrainteractive.astrarating.dto.RatingType
import com.astrainteractive.astrarating.dto.UserDTO
import com.astrainteractive.astrarating.events.di.EventModule
import com.astrainteractive.astrarating.models.UserModel
import kotlinx.coroutines.launch
import org.bukkit.event.entity.PlayerDeathEvent
import ru.astrainteractive.astralibs.event.DSLEvent

class KillEventListener(
    module: EventModule
) : EventModule by module {

    val onPlayerKilledPlayer = DSLEvent<PlayerDeathEvent>(eventListener, plugin) {
        if (!configDependency.events.killPlayer.enabled) return@DSLEvent
        if (configDependency.events.killPlayer.changeBy == 0) return@DSLEvent
        val killedPlayer = it.entity
        val killerPlayer = killedPlayer.killer ?: return@DSLEvent
        scope.launch(dispatchers.IO) {
            val playerDTO = apiDependency.selectUser(killerPlayer.name).getOrNull() ?: run {
                val user = UserModel(
                    minecraftUUID = killerPlayer.uniqueId,
                    minecraftName = killerPlayer.name
                )
                val id = apiDependency.insertUser(user).getOrNull() ?: return@launch
                UserDTO(
                    id = id,
                    minecraftName = user.minecraftName,
                    minecraftUUID = user.minecraftUUID.toString()
                )
            }
            apiDependency.insertUserRating(
                reporter = null,
                reported = playerDTO,
                message = translationDependency.killedPlayer(killedPlayer.name),
                type = RatingType.PLAYER_KILL,
                ratingValue = configDependency.events.killPlayer.changeBy
            )
        }
    }
}
