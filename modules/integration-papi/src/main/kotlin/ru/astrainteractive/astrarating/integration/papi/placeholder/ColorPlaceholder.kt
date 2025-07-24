package ru.astrainteractive.astrarating.integration.papi.placeholder

import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import ru.astrainteractive.astrarating.core.cache.Cache4kCache
import ru.astrainteractive.astrarating.integration.papi.coloring.ColoringUtil
import ru.astrainteractive.astrarating.integration.papi.di.PapiDependencies
import ru.astrainteractive.astrarating.integration.papi.placeholder.api.RatingPlaceholder
import java.util.UUID
import kotlin.time.Duration.Companion.seconds

internal class ColorPlaceholder(
    dependencies: PapiDependencies
) : RatingPlaceholder, PapiDependencies by dependencies {

    private val jcache = Cache4kCache<UUID, String>(
        expiresAfterAccess = 30.seconds,
        updateAfterAccess = 10.seconds,
        maximumSize = 100L,
        coroutineScope = scope,
        update = { uuid ->
            val name = Bukkit.getOfflinePlayer(uuid).name ?: Bukkit.getPlayer(uuid)?.name
            name ?: return@Cache4kCache ""
            val rating = ratingCachedDao.getPlayerRating(name, uuid)

            ColoringUtil.getColoringByRating(
                colorings = papiConfiguration.colorings,
                rating = rating
            ).color
        }
    )

    override val key: String = "color"

    override fun asPlaceholder(param: OfflinePlayer): String {
        return jcache.getIfPresent(param.uniqueId).orEmpty()
    }
}
