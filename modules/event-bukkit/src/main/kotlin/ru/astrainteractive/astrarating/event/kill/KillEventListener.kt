package ru.astrainteractive.astrarating.event.kill

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.PlayerDeathEvent
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.kyori.unwrap
import ru.astrainteractive.astrarating.core.settings.AstraRatingConfig
import ru.astrainteractive.astrarating.core.settings.AstraRatingTranslation
import ru.astrainteractive.astrarating.data.dao.RatingDao
import ru.astrainteractive.astrarating.data.dao.upsertUser
import ru.astrainteractive.astrarating.data.exposed.dto.RatingType
import ru.astrainteractive.astrarating.data.exposed.model.UserModel
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.util.getValue
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

internal class KillEventListener(
    configKrate: CachedKrate<AstraRatingConfig>,
    translationKrate: CachedKrate<AstraRatingTranslation>,
    val kyoriKrate: CachedKrate<KyoriComponentSerializer>,
    val ratingDao: RatingDao,
    val scope: CoroutineScope,
    val dispatchers: KotlinDispatchers
) : EventListener, KyoriComponentSerializer by kyoriKrate.unwrap() {
    private val config by configKrate
    private val translation by translationKrate

    @EventHandler
    fun onPlayerKilledPlayer(e: PlayerDeathEvent) {
        if (!config.events.killPlayer.enabled) return
        if (config.events.killPlayer.changeBy == 0) return
        val killedPlayer = e.entity
        val killerPlayer = killedPlayer.killer ?: return

        scope.launch(dispatchers.IO) {
            val killedPlayerRating = ratingDao.fetchUsersTotalRating().getOrNull().orEmpty()
                .firstOrNull { it.userDTO.minecraftUUID == killedPlayer.uniqueId.toString() }
                ?.ratingTotal
                ?: error("Could not fetch rating of ${killedPlayer.name}")
            if (killedPlayerRating <= 0) return@launch

            ratingDao.insertUserRating(
                reporter = null,
                reported = ratingDao.upsertUser(
                    userModel = UserModel(
                        minecraftUUID = killerPlayer.uniqueId,
                        minecraftName = killerPlayer.name
                    )
                ),
                message = translation.gui.killedPlayer(killedPlayer.name).raw,
                type = RatingType.PLAYER_KILL,
                ratingValue = config.events.killPlayer.changeBy
            )
            translation.messages.youKilledPlayer(killedPlayer.name)
                .component
                .run(killerPlayer::sendMessage)
        }
    }
}
