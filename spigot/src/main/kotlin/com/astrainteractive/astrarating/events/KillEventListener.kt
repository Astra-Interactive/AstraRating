package com.astrainteractive.astrarating.events

import com.astrainteractive.astrarating.domain.api.RatingDBApi
import com.astrainteractive.astrarating.dto.RatingType
import com.astrainteractive.astrarating.dto.UserDTO
import com.astrainteractive.astrarating.models.UserModel
import com.astrainteractive.astrarating.plugin.EmpireConfig
import com.astrainteractive.astrarating.plugin.PluginTranslation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bukkit.event.entity.PlayerDeathEvent
import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.di.Dependency
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astralibs.events.DSLEvent

class KillEventListener(
    configDependency: Dependency<EmpireConfig>,
    apiDependency: Dependency<RatingDBApi>,
    translationDependency: Dependency<PluginTranslation>
) {
    private val config by configDependency
    private val api by apiDependency
    private val translation by translationDependency

    val onPlayerKilledPlayer = DSLEvent.event<PlayerDeathEvent> {
        if (!config.events.killPlayer.enabled) return@event
        if (config.events.killPlayer.changeBy == 0) return@event
        val killedPlayer = it.entity
        val killerPlayer = killedPlayer.killer ?: return@event
        PluginScope.launch(Dispatchers.IO) {
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