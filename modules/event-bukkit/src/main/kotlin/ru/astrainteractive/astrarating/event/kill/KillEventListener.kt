package ru.astrainteractive.astrarating.event.kill

import kotlinx.coroutines.launch
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.PlayerDeathEvent
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.astrarating.api.rating.api.RatingDBApiExt.upsertUser
import ru.astrainteractive.astrarating.dto.RatingType
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
            val killedPlayerRating = apiDependency.fetchUsersTotalRating().getOrNull().orEmpty()
                .firstOrNull { it.userDTO.minecraftUUID == killedPlayer.uniqueId.toString() }
                ?.ratingTotal
                ?: error("Could not fetch rating of ${killedPlayer.name}")
            if (killedPlayerRating <= 0) return@launch

            apiDependency.insertUserRating(
                reporter = null,
                reported = apiDependency.upsertUser(
                    userModel = UserModel(
                        minecraftUUID = killerPlayer.uniqueId,
                        minecraftName = killerPlayer.name
                    )
                ),
                message = translationDependency.killedPlayer(killedPlayer.name).raw,
                type = RatingType.PLAYER_KILL,
                ratingValue = configDependency.events.killPlayer.changeBy
            )
            translationDependency.youKilledPlayer(killedPlayer.name)
                .let(translationContext::toComponent)
                .run(killerPlayer::sendMessage)
        }
    }
}
