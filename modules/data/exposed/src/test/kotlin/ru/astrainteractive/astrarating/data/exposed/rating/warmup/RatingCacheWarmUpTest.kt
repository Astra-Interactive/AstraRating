@file:Suppress("FunctionNaming")

package ru.astrainteractive.astrarating.data.exposed.rating.warmup

import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import ru.astrainteractive.astralibs.server.player.OnlineKPlayer
import ru.astrainteractive.astrarating.data.exposed.rating.fake.FakeRatingCache
import ru.astrainteractive.astrarating.data.exposed.rating.warmup.fake.FakeOnlineKPlayer
import ru.astrainteractive.astrarating.data.exposed.rating.warmup.fake.FakePlatformServer
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals

internal class RatingCacheWarmUpTest {
    private val ratingCache = FakeRatingCache()
    private val tester = FakeOnlineKPlayer(UUID.fromString("f3d28cb0-7225-3cb1-baeb-2dadd2be89ae"), "tester")
    private val builder = FakeOnlineKPlayer(UUID.fromString("0b9f4d1e-0c7a-3b8a-9d65-2c2f0f3e9a11"), "builder")

    private fun TestScope.createWarmUp(onlinePlayers: List<OnlineKPlayer>): RatingCacheWarmUp {
        return RatingCacheWarmUp(
            platformServer = FakePlatformServer(onlinePlayers),
            ratingCache = ratingCache,
            ioScope = backgroundScope
        )
    }

    @Test
    fun GIVEN_players_online_WHEN_plugin_is_enabled_THEN_rating_of_every_player_is_loaded() = runTest {
        val warmUp = createWarmUp(listOf(tester, builder))

        warmUp.onEnable()
        runCurrent()

        assertEquals(setOf(tester.uuid, builder.uuid), ratingCache.loadedPlayers.toSet())
    }

    @Test
    fun GIVEN_nobody_online_WHEN_plugin_is_enabled_THEN_no_rating_is_loaded() = runTest {
        val warmUp = createWarmUp(emptyList())

        warmUp.onEnable()
        runCurrent()

        assertEquals(emptyList(), ratingCache.loadedPlayers)
    }
}
