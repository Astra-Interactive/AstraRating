@file:Suppress("FunctionNaming")

package ru.astrainteractive.astrarating.event.kill.usecase

import kotlinx.coroutines.test.runTest
import ru.astrainteractive.astrarating.core.settings.model.AstraRatingConfig
import ru.astrainteractive.astrarating.data.dao.rating.fake.FakeRatingRepository
import ru.astrainteractive.astrarating.data.dao.rating.model.NewUser
import ru.astrainteractive.astrarating.data.dao.rating.model.RatingType
import ru.astrainteractive.astrarating.event.kill.model.LowerKillerRatingResult
import ru.astrainteractive.klibs.kstorage.api.asCachedMutableKrate
import ru.astrainteractive.klibs.kstorage.api.impl.DefaultMutableKrate
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals

class LowerKillerRatingUseCaseTest {
    private val repository = FakeRatingRepository()
    private val configKrate = DefaultMutableKrate(
        factory = { killEventConfig(enabled = true, changeBy = -2) },
        loader = { null }
    ).asCachedMutableKrate()
    private val useCase = LowerKillerRatingUseCase(
        ratingRepository = repository,
        configKrate = configKrate
    )
    private val killer = NewUser(
        minecraftUUID = UUID.fromString("f3d28cb0-7225-3cb1-baeb-2dadd2be89ae"),
        minecraftName = "Killer"
    )
    private val killedPlayerUUID = UUID.fromString("5a6a6c1e-2f9b-3d17-9a5b-1d2a6f2b7c11")

    private fun killEventConfig(enabled: Boolean, changeBy: Int): AstraRatingConfig {
        return AstraRatingConfig(
            events = AstraRatingConfig.Events(
                killPlayer = AstraRatingConfig.Events.Event(changeBy = changeBy, enabled = enabled)
            )
        )
    }

    private suspend fun lowerKillerRating(): LowerKillerRatingResult {
        return useCase.invoke(
            killer = killer,
            killedPlayerUUID = killedPlayerUUID,
            reason = REASON
        )
    }

    @Test
    fun GIVEN_rated_victim_WHEN_killed_THEN_killer_gets_the_configured_change() {
        runTest {
            repository.setTotalRating(killedPlayerUUID, 3)

            val result = lowerKillerRating()

            assertEquals(LowerKillerRatingResult.Lowered, result)
            val inserted = repository.insertedRatings.single()
            assertEquals(null, inserted.reporter)
            assertEquals(killer.minecraftUUID.toString(), inserted.reported.minecraftUUID)
            assertEquals(killer.minecraftName, inserted.reported.minecraftName)
            assertEquals(REASON, inserted.message)
            assertEquals(RatingType.PLAYER_KILL, inserted.type)
            assertEquals(-2, inserted.ratingValue)
        }
    }

    @Test
    fun GIVEN_kill_event_disabled_WHEN_rated_victim_is_killed_THEN_nothing_is_stored() {
        runTest {
            repository.setTotalRating(killedPlayerUUID, 3)
            configKrate.save(killEventConfig(enabled = false, changeBy = -2))

            val result = lowerKillerRating()

            assertEquals(LowerKillerRatingResult.Disabled, result)
            assertEquals(emptyList(), repository.insertedRatings)
        }
    }

    @Test
    fun GIVEN_kill_event_changes_nothing_WHEN_rated_victim_is_killed_THEN_nothing_is_stored() {
        runTest {
            repository.setTotalRating(killedPlayerUUID, 3)
            configKrate.save(killEventConfig(enabled = true, changeBy = 0))

            val result = lowerKillerRating()

            assertEquals(LowerKillerRatingResult.Disabled, result)
            assertEquals(emptyList(), repository.insertedRatings)
        }
    }

    @Test
    fun GIVEN_victim_with_zero_rating_WHEN_killed_THEN_killer_keeps_rating() {
        runTest {
            repository.setTotalRating(killedPlayerUUID, 0)

            val result = lowerKillerRating()

            assertEquals(LowerKillerRatingResult.KilledPlayerNotRated, result)
            assertEquals(emptyList(), repository.insertedRatings)
        }
    }

    @Test
    fun GIVEN_victim_with_negative_rating_WHEN_killed_THEN_killer_keeps_rating() {
        runTest {
            repository.setTotalRating(killedPlayerUUID, -4)

            val result = lowerKillerRating()

            assertEquals(LowerKillerRatingResult.KilledPlayerNotRated, result)
            assertEquals(emptyList(), repository.insertedRatings)
        }
    }

    @Test
    fun GIVEN_config_reloaded_WHEN_rated_victim_is_killed_THEN_new_change_is_used() {
        runTest {
            repository.setTotalRating(killedPlayerUUID, 3)
            configKrate.save(killEventConfig(enabled = true, changeBy = -5))

            lowerKillerRating()

            assertEquals(-5, repository.insertedRatings.single().ratingValue)
        }
    }

    @Test
    fun GIVEN_victim_rating_can_not_be_read_WHEN_killed_THEN_storage_failed() {
        runTest {
            repository.setTotalRating(killedPlayerUUID, 3)
            repository.failingCalls += FakeRatingRepository.Call.FETCH_TOTAL_RATING

            val result = lowerKillerRating()

            assertEquals(LowerKillerRatingResult.StorageFailed, result)
            assertEquals(emptyList(), repository.insertedRatings)
        }
    }

    @Test
    fun GIVEN_killer_can_not_be_stored_WHEN_rated_victim_is_killed_THEN_storage_failed() {
        runTest {
            repository.setTotalRating(killedPlayerUUID, 3)
            repository.failingCalls += FakeRatingRepository.Call.INSERT_USER

            val result = lowerKillerRating()

            assertEquals(LowerKillerRatingResult.StorageFailed, result)
            assertEquals(emptyList(), repository.insertedRatings)
        }
    }

    @Test
    fun GIVEN_rating_can_not_be_stored_WHEN_rated_victim_is_killed_THEN_storage_failed() {
        runTest {
            repository.setTotalRating(killedPlayerUUID, 3)
            repository.failingCalls += FakeRatingRepository.Call.INSERT_RATING

            val result = lowerKillerRating()

            assertEquals(LowerKillerRatingResult.StorageFailed, result)
        }
    }

    companion object {
        private const val REASON = "Killed Victim"
    }
}
