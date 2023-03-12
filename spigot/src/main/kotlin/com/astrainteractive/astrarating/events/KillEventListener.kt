package com.astrainteractive.astrarating.events

import com.astrainteractive.astrarating.domain.api.IRatingAPI
import com.astrainteractive.astrarating.domain.entities.tables.dto.RatingTypeDTO
import com.astrainteractive.astrarating.domain.entities.tables.dto.UserDTO
import com.astrainteractive.astrarating.domain.entities.tables.dto.UserRatingDTO
import com.astrainteractive.astrarating.utils.EmpireConfig
import com.astrainteractive.astrarating.utils.PluginTranslation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.PlayerDeathEvent
import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.di.IDependency
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astralibs.events.DSLEvent
import ru.astrainteractive.astralibs.utils.uuid

class KillEventListener(
    configDependency: IDependency<EmpireConfig>,
    apiDependency: IDependency<IRatingAPI>,
    translationDependency: IDependency<PluginTranslation>
) {
    private val config by configDependency
    private val api by apiDependency
    private val translation by translationDependency

    val onPlayerKilledPlayer = DSLEvent.event<PlayerDeathEvent> {
        println("onPlayerKilledPlayer")
        if (!config.events.killPlayer.enabled) return@event
        if (config.events.killPlayer.changeBy == 0) return@event
        val killedPlayer = it.entity
        val killerPlayer = killedPlayer.killer ?: return@event
        PluginScope.launch(Dispatchers.IO) {
            val playerDTO = api.selectUser(killerPlayer.name) ?: run {
                val user = UserDTO(
                    minecraftUUID = killerPlayer.uuid,
                    minecraftName = killerPlayer.name
                )
                val id = api.insertUser(user) ?: return@launch
                user.copy(id = id)
            }
            val userRatingDTO = UserRatingDTO(
                userCreatedReport = null,
                reportedUser = playerDTO.id,
                rating = config.events.killPlayer.changeBy,
                message = translation.killedPlayer(killedPlayer.name),
                ratingType = RatingTypeDTO.PLAYER_KILL
            )
            api.insertUserRating(userRatingDTO)
        }
    }
}