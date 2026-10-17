package ru.astrainteractive.astrarating.event.kill

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.PlayerDeathEvent
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.astralibs.server.util.asKAudience
import ru.astrainteractive.astrarating.core.settings.AstraRatingConfig
import ru.astrainteractive.astrarating.core.settings.AstraRatingTranslation
import ru.astrainteractive.astrarating.data.dao.rating.api.RatingRepository
import ru.astrainteractive.astrarating.data.dao.rating.model.NewUser
import ru.astrainteractive.astrarating.data.dao.rating.model.RatingType
import ru.astrainteractive.astrarating.data.dao.rating.util.upsertUser
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.getValue
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import java.util.Locale

internal class KillEventListener(
    configKrate: CachedKrate<AstraRatingConfig>,
    translationKrate: CachedKrate<AstraRatingTranslation>,
    val ratingRepository: RatingRepository,
    val scope: CoroutineScope,
    val dispatchers: KotlinDispatchers
) : EventListener {
    private val config by configKrate
    private val translation by translationKrate

    /** The reason is stored once and read by everyone, so it keeps the default language and no markup. */
    private fun killReason(killedPlayerName: String): String {
        val component = translation.playerKill.reason(killedPlayerName).toComponent(Locale.ROOT)
        return PlainTextComponentSerializer.plainText().serialize(component)
    }

    @EventHandler
    fun onPlayerKilledPlayer(e: PlayerDeathEvent) {
        if (!config.events.killPlayer.enabled) return
        if (config.events.killPlayer.changeBy == 0) return
        val killedPlayer = e.entity
        val killerPlayer = killedPlayer.killer ?: return

        scope.launch(dispatchers.IO) {
            val killedPlayerRating = ratingRepository.fetchUsersTotalRating().getOrNull().orEmpty()
                .firstOrNull { userRating -> userRating.user.minecraftUUID == killedPlayer.uniqueId.toString() }
                ?.ratingTotal
                ?: error("Could not fetch rating of ${killedPlayer.name}")
            if (killedPlayerRating <= 0) return@launch

            ratingRepository.insertUserRating(
                reporter = null,
                reported = ratingRepository.upsertUser(
                    newUser = NewUser(
                        minecraftUUID = killerPlayer.uniqueId,
                        minecraftName = killerPlayer.name
                    )
                ),
                message = killReason(killedPlayer.name),
                type = RatingType.PLAYER_KILL,
                ratingValue = config.events.killPlayer.changeBy
            )
            killerPlayer.asKAudience().sendMessage(translation.playerKill.ratingLowered(killedPlayer.name))
        }
    }
}
