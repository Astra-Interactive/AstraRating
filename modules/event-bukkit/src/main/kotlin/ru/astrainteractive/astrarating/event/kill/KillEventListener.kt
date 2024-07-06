package ru.astrainteractive.astrarating.event.kill

import kotlinx.coroutines.launch
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.PlayerDeathEvent
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.astrarating.dto.RatingType
import ru.astrainteractive.astrarating.dto.UserDTO
import ru.astrainteractive.astrarating.event.di.EventDependencies
import ru.astrainteractive.astrarating.model.UserModel

internal class KillEventListener(
    module: EventDependencies
) : EventDependencies by module,
    EventListener {
    @EventHandler
    fun onPlayerKilledPlayer(e: PlayerDeathEvent) {
        if (!configDependency.events.killPlayer.enabled) return
        if (configDependency.events.killPlayer.changeBy == 0) return
        val killedPlayer = e.entity
        val killerPlayer = killedPlayer.killer ?: return
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
                message = translationDependency.killedPlayer(killedPlayer.name).raw,
                type = RatingType.PLAYER_KILL,
                ratingValue = configDependency.events.killPlayer.changeBy
            )
        }
    }
}
