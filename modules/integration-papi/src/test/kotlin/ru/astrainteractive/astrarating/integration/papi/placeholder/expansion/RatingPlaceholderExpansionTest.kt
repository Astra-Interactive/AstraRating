@file:Suppress("FunctionNaming")

package ru.astrainteractive.astrarating.integration.papi.placeholder.expansion

import org.bukkit.OfflinePlayer
import ru.astrainteractive.astrarating.data.dao.rating.api.RatingCache
import ru.astrainteractive.astrarating.integration.papi.placeholder.model.Coloring
import ru.astrainteractive.astrarating.integration.papi.placeholder.model.PapiConfig
import ru.astrainteractive.klibs.kstorage.api.asCachedMutableKrate
import ru.astrainteractive.klibs.kstorage.api.impl.DefaultMutableKrate
import java.lang.reflect.Proxy
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class RatingPlaceholderExpansionTest {
    private val playerUUID = UUID.fromString("f3d28cb0-7225-3cb1-baeb-2dadd2be89ae")
    private val ratingCache = object : RatingCache {
        override fun resolvePlayerRating(uuid: UUID): Int = if (uuid == playerUUID) 7 else 0

        override suspend fun loadPlayerRating(uuid: UUID) = Unit

        override fun clear() = Unit
    }
    private val papiConfig = PapiConfig(
        colorings = listOf(
            Coloring.Less(value = 0, color = "#FF0000"),
            Coloring.More(value = 0, color = "#00FF00")
        )
    )
    private val papiConfigKrate = DefaultMutableKrate(
        factory = { papiConfig },
        loader = { null }
    ).asCachedMutableKrate()
    private val expansion = RatingPlaceholderExpansion(
        placeholders = listOf(
            RatingAmountPlaceholder(ratingCache = ratingCache),
            ColorPlaceholder(ratingCache = ratingCache, papiConfigKrate = papiConfigKrate)
        )
    )

    private fun offlinePlayer(uuid: UUID): OfflinePlayer {
        val proxy = Proxy.newProxyInstance(OfflinePlayer::class.java.classLoader, arrayOf(OfflinePlayer::class.java)) {
                _, method, _ ->
            if (method.name == "getUniqueId") uuid else error("The tests do not call ${method.name}")
        }
        return proxy as OfflinePlayer
    }

    @Test
    fun GIVEN_plugin_registered_expansion_WHEN_placeholderapi_reloads_THEN_expansion_is_kept() {
        assertTrue(expansion.persist())
    }

    @Test
    fun GIVEN_no_player_WHEN_rating_is_requested_THEN_placeholder_is_left_unparsed() {
        assertNull(expansion.onRequest(player = null, params = "rating"))
    }

    @Test
    fun GIVEN_cached_rating_WHEN_rating_is_requested_THEN_rating_is_returned() {
        assertEquals("7", expansion.onRequest(player = offlinePlayer(playerUUID), params = "rating"))
    }

    @Test
    fun GIVEN_positive_rating_WHEN_color_is_requested_THEN_positive_color_is_returned() {
        assertEquals("#00FF00", expansion.onRequest(player = offlinePlayer(playerUUID), params = "color"))
    }

    @Test
    fun GIVEN_uncached_zero_rating_WHEN_color_is_requested_THEN_no_color_is_returned() {
        assertEquals("", expansion.onRequest(player = offlinePlayer(UUID.randomUUID()), params = "color"))
    }

    @Test
    fun GIVEN_unknown_placeholder_WHEN_requested_THEN_empty_text_is_returned() {
        assertEquals("", expansion.onRequest(player = offlinePlayer(playerUUID), params = "unknown"))
    }
}
