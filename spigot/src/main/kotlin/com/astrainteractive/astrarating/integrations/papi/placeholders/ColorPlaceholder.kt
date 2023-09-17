package com.astrainteractive.astrarating.integrations.papi.placeholders

import com.astrainteractive.astrarating.domain.util.cache.JCache
import com.astrainteractive.astrarating.integrations.papi.coloring.ColoringMapper
import com.astrainteractive.astrarating.integrations.papi.coloring.ColoringUtils
import com.astrainteractive.astrarating.integrations.papi.di.PapiModule
import com.astrainteractive.astrarating.integrations.papi.placeholders.api.RatingPlaceholder
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import ru.astrainteractive.astralibs.util.hex
import java.util.UUID
import kotlin.time.Duration.Companion.seconds

class ColorPlaceholder(
    module: PapiModule
) : RatingPlaceholder, PapiModule by module {

    private val jcache = JCache<UUID, String>(
        expiresAfterAccess = 30.seconds,
        updateAfterAccess = 10.seconds,
        maximumSize = 100L,
        coroutineScope = scope,
        update = { uuid ->
            val name = Bukkit.getOfflinePlayer(uuid).name ?: Bukkit.getPlayer(uuid)?.name
            name ?: return@JCache ""
            val rating = cachedApi.getPlayerRating(name, uuid)
            val coloring = config.coloring.map(ColoringMapper::toDTO)
            val color = ColoringUtils.getColoringByRating(coloring, rating).color
            color
        }
    )

    override val key: String = "color"

    override fun asPlaceholder(param: OfflinePlayer): String {
        return jcache.getIfPresent(param.uniqueId)?.hex() ?: ""
    }
}
