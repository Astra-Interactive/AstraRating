@file:Suppress("FunctionNaming")

package ru.astrainteractive.astrarating.data.dao.rating.util

import kotlinx.coroutines.test.runTest
import ru.astrainteractive.astrarating.data.dao.rating.fake.FakeRatingRepository
import ru.astrainteractive.astrarating.data.dao.rating.model.NewUser
import ru.astrainteractive.astrarating.data.dao.rating.model.RatingStorageError
import ru.astrainteractive.astrarating.data.dao.rating.model.User
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.time.Instant

class RatingRepositoryExtTest {
    private val repository = FakeRatingRepository()
    private val playerUUID = UUID.fromString("f3d28cb0-7225-3cb1-baeb-2dadd2be89ae")
    private val storedUser = User(
        id = 7L,
        minecraftUUID = playerUUID.toString(),
        minecraftName = "Tester",
        lastUpdated = Instant.fromEpochMilliseconds(0L)
    )

    @Test
    fun GIVEN_unknown_player_WHEN_user_is_upserted_THEN_user_is_inserted() {
        runTest {
            val user = repository.upsertUser(NewUser(minecraftUUID = playerUUID, minecraftName = "Tester")).getOrThrow()

            assertEquals("Tester", user.minecraftName)
            assertEquals(user, repository.findUser(playerUUID).getOrThrow())
        }
    }

    @Test
    fun GIVEN_stored_player_WHEN_user_is_upserted_with_same_name_THEN_stored_user_is_returned() {
        runTest {
            repository.storeUser(storedUser)

            val user = repository.upsertUser(NewUser(minecraftUUID = playerUUID, minecraftName = "Tester")).getOrThrow()

            assertEquals(storedUser, user)
        }
    }

    @Test
    fun GIVEN_stored_player_WHEN_user_is_upserted_with_new_name_THEN_name_is_updated() {
        runTest {
            repository.storeUser(storedUser)

            val user = repository.upsertUser(
                NewUser(
                    minecraftUUID = playerUUID,
                    minecraftName = "Renamed"
                )
            ).getOrThrow()

            assertEquals(storedUser.copy(minecraftName = "Renamed"), user)
            assertEquals("Renamed", repository.findUser(playerUUID).getOrThrow()?.minecraftName)
        }
    }

    @Test
    fun GIVEN_unavailable_storage_WHEN_user_is_upserted_THEN_failure_is_returned() {
        runTest {
            repository.isAvailable = false

            val result = repository.upsertUser(NewUser(minecraftUUID = playerUUID, minecraftName = "Tester"))

            assertIs<RatingStorageError.DatabaseUnavailable>(result.exceptionOrNull())
        }
    }
}
