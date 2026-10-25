package ru.astrainteractive.astrarating.event.kill.event

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.PlayerDeathEvent
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.astralibs.server.util.asKAudience
import ru.astrainteractive.astrarating.core.settings.model.AstraRatingTranslation
import ru.astrainteractive.astrarating.data.dao.rating.model.NewUser
import ru.astrainteractive.astrarating.event.kill.model.LowerKillerRatingResult
import ru.astrainteractive.astrarating.event.kill.usecase.LowerKillerRatingUseCase
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.getValue
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import java.util.Locale

internal class KillEventListener(
    translationKrate: CachedKrate<AstraRatingTranslation>,
    private val lowerKillerRatingUseCase: LowerKillerRatingUseCase,
    private val scope: CoroutineScope,
    private val dispatchers: KotlinDispatchers
) : EventListener {
    private val translation by translationKrate

    /** The reason is stored once and read by everyone, so it keeps the default language and no markup. */
    private fun killReason(killedPlayerName: String): String {
        val component = translation.playerKill.reason(killedPlayerName).toComponent(Locale.ROOT)
        return PlainTextComponentSerializer.plainText().serialize(component)
    }

    @EventHandler
    fun onPlayerKilledPlayer(event: PlayerDeathEvent) {
        val killedPlayer = event.entity
        val killerPlayer = killedPlayer.killer ?: return

        scope.launch(dispatchers.IO) {
            val result = lowerKillerRatingUseCase.invoke(
                killer = NewUser(
                    minecraftUUID = killerPlayer.uniqueId,
                    minecraftName = killerPlayer.name
                ),
                killedPlayerUUID = killedPlayer.uniqueId,
                reason = killReason(killedPlayer.name)
            )
            if (result == LowerKillerRatingResult.Lowered) {
                killerPlayer.asKAudience().sendMessage(translation.playerKill.ratingLowered(killedPlayer.name))
            }
        }
    }
}
