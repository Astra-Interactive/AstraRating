package com.astrainteractive.astrarating.events

import com.astrainteractive.astrarating.dto.RatingType
import com.astrainteractive.astrarating.dto.UserDTO
import com.astrainteractive.astrarating.events.di.EventModule
import com.astrainteractive.astrarating.models.UserModel
import kotlinx.coroutines.launch
import org.bukkit.event.entity.PlayerDeathEvent
import ru.astrainteractive.astralibs.events.DSLEvent
import ru.astrainteractive.astralibs.getValue

class KillEventListener(
    module: EventModule
) {
    private val config by module.configDependency
    private val api by module.apiDependency
    private val translation by module.translationDependency
    private val scope by module.scope
    private val eventListener by module.eventListener
    private val plugin by module.plugin
    private val dispatchers by module.dispatchers

    val onPlayerKilledPlayer = DSLEvent<PlayerDeathEvent>(eventListener, plugin) {
        if (!config.events.killPlayer.enabled) return@DSLEvent
        if (config.events.killPlayer.changeBy == 0) return@DSLEvent
        val killedPlayer = it.entity
        val killerPlayer = killedPlayer.killer ?: return@DSLEvent
        scope.launch(dispatchers.IO) {
            val playerDTO = api.selectUser(killerPlayer.name).getOrNull() ?: run {
                val user = UserModel(
                    minecraftUUID = killerPlayer.uniqueId,
                    minecraftName = killerPlayer.name
                )
                val id = api.insertUser(user).getOrNull() ?: return@launch
                UserDTO(
                    id = id,
                    minecraftName = user.minecraftName,
                    minecraftUUID = user.minecraftUUID.toString()
                )
            }
            api.insertUserRating(
                reporter = null,
                reported = playerDTO,
                message = translation.killedPlayer(killedPlayer.name),
                type = RatingType.PLAYER_KILL,
                ratingValue = config.events.killPlayer.changeBy
            )
        }
    }
}
