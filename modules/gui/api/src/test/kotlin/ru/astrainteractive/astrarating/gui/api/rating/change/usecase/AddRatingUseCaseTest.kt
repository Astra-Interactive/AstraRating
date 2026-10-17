@file:Suppress("FunctionNaming")

package ru.astrainteractive.astrarating.gui.api.rating.change.usecase

import kotlinx.coroutines.test.runTest
import ru.astrainteractive.astralibs.server.permission.Permission
import ru.astrainteractive.astrarating.core.settings.AstraRatingConfig
import ru.astrainteractive.astrarating.core.settings.AstraRatingPermission
import ru.astrainteractive.astrarating.data.dao.rating.fake.FakeRatingRepository
import ru.astrainteractive.astrarating.data.dao.rating.model.RatingType
import ru.astrainteractive.astrarating.data.dao.rating.model.User
import ru.astrainteractive.astrarating.gui.api.rating.change.fake.FakeKPermissible
import ru.astrainteractive.astrarating.gui.api.rating.change.model.AddRatingResult
import ru.astrainteractive.astrarating.gui.api.rating.change.model.RatingPlayer
import ru.astrainteractive.klibs.kstorage.api.asCachedMutableKrate
import ru.astrainteractive.klibs.kstorage.api.impl.DefaultMutableKrate
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals

class AddRatingUseCaseTest {
    private val repository = FakeRatingRepository()
    private val config = AstraRatingConfig(
        maxRatingPerDay = 2,
        maxRatingPerPlayer = 1,
        minMessageLength = 3,
        maxMessageLength = 10
    )
    private val useCase = AddRatingUseCase(
        ratingRepository = repository,
        configKrate = DefaultMutableKrate(factory = { config }, loader = { null }).asCachedMutableKrate()
    )
    private val creator = player(
        name = "Voter",
        uuid = "f3d28cb0-7225-3cb1-baeb-2dadd2be89ae",
        grantedPermissions = setOf(AstraRatingPermission.Vote)
    )
    private val ratedPlayer = player(name = "Rated", uuid = "5a6a6c1e-2f9b-3d17-9a5b-1d2a6f2b7c11")

    private fun player(
        name: String,
        uuid: String,
        grantedPermissions: Set<Permission> = emptySet(),
        permissionSizes: Map<Permission, Int> = emptyMap(),
        hasPlayedBefore: Boolean = true
    ): RatingPlayer = RatingPlayer(
        uuid = UUID.fromString(uuid),
        name = name,
        permissible = FakeKPermissible(grantedPermissions, permissionSizes),
        hasPlayedBefore = hasPlayedBefore
    )

    private fun storedUser(player: RatingPlayer): User {
        val user = User(id = 10L, minecraftUUID = player.uuid.toString(), minecraftName = player.name, lastUpdated = 0L)
        repository.storeUser(user)
        return user
    }

    private suspend fun rate(
        creator: RatingPlayer = this.creator,
        ratedPlayer: RatingPlayer? = this.ratedPlayer,
        message: String = "good game"
    ): AddRatingResult = useCase(
        creator = creator,
        ratedPlayer = ratedPlayer,
        message = message,
        rating = 1,
        type = RatingType.USER_RATING
    )

    @Test
    fun GIVEN_allowed_vote_WHEN_rating_is_added_THEN_rating_from_creator_to_rated_player_is_stored() = runTest {
        val result = rate()

        assertEquals(AddRatingResult.Success, result)
        val inserted = repository.insertedRatings.single()
        assertEquals(creator.uuid.toString(), inserted.reporter?.minecraftUUID)
        assertEquals(ratedPlayer.uuid.toString(), inserted.reported.minecraftUUID)
        assertEquals("good game", inserted.message)
        assertEquals(RatingType.USER_RATING, inserted.type)
        assertEquals(1, inserted.ratingValue)
    }

    @Test
    fun GIVEN_unknown_rated_player_WHEN_rating_is_added_THEN_player_not_exists() = runTest {
        assertEquals(AddRatingResult.PlayerNotExists, rate(ratedPlayer = null))
    }

    @Test
    fun GIVEN_creator_without_vote_permission_WHEN_rating_is_added_THEN_nothing_is_stored() = runTest {
        val result = rate(creator = player(name = "Voter", uuid = creator.uuid.toString()))

        assertEquals(AddRatingResult.NoPermission, result)
        assertEquals(emptyList(), repository.insertedRatings)
    }

    @Test
    fun GIVEN_creator_without_permissions_object_WHEN_rating_is_added_THEN_no_permission() = runTest {
        val result = rate(creator = creator.copy(permissible = null))

        assertEquals(AddRatingResult.NoPermission, result)
    }

    @Test
    fun GIVEN_rated_player_never_joined_WHEN_rating_is_added_THEN_player_not_exists() = runTest {
        val result = rate(ratedPlayer = ratedPlayer.copy(hasPlayedBefore = false))

        assertEquals(AddRatingResult.PlayerNotExists, result)
    }

    @Test
    fun GIVEN_creator_rates_themselves_WHEN_rating_is_added_THEN_same_player() = runTest {
        assertEquals(AddRatingResult.SamePlayer, rate(ratedPlayer = creator))
    }

    @Test
    fun GIVEN_creator_used_daily_votes_from_config_WHEN_rating_is_added_THEN_already_max_day_votes() = runTest {
        repository.setDayVotes(creator.uuid, count = 2)

        assertEquals(AddRatingResult.AlreadyMaxDayVotes, rate())
    }

    @Test
    fun GIVEN_permission_raises_daily_votes_WHEN_rating_is_added_over_config_limit_THEN_success() = runTest {
        val creator = creator.copy(
            permissible = FakeKPermissible(
                grantedPermissions = setOf(AstraRatingPermission.Vote),
                permissionSizes = mapOf(AstraRatingPermission.MaxRatePerDay to 5)
            )
        )
        repository.setDayVotes(creator.uuid, count = 4)

        assertEquals(AddRatingResult.Success, rate(creator = creator))
    }

    @Test
    fun GIVEN_creator_already_voted_on_player_today_WHEN_rating_is_added_THEN_already_max_votes_on_player() = runTest {
        repository.setDayVotesOnPlayer(creator.uuid, ratedPlayer.uuid, count = 1)

        assertEquals(AddRatingResult.AlreadyMaxVotesOnPlayer, rate())
    }

    @Test
    fun GIVEN_permission_raises_votes_on_player_WHEN_rating_is_added_again_THEN_success() = runTest {
        val creator = creator.copy(
            permissible = FakeKPermissible(
                grantedPermissions = setOf(AstraRatingPermission.Vote),
                permissionSizes = mapOf(AstraRatingPermission.SinglePlayerPerDay to 3)
            )
        )
        repository.setDayVotesOnPlayer(creator.uuid, ratedPlayer.uuid, count = 2)

        assertEquals(AddRatingResult.Success, rate(creator = creator))
    }

    @Test
    fun GIVEN_message_shorter_than_minimum_WHEN_rating_is_added_THEN_message_not_correct() = runTest {
        assertEquals(AddRatingResult.MessageNotCorrect, rate(message = "gg"))
    }

    @Test
    fun GIVEN_message_longer_than_maximum_WHEN_rating_is_added_THEN_message_not_correct() = runTest {
        assertEquals(AddRatingResult.MessageNotCorrect, rate(message = "a".repeat(11)))
    }

    @Test
    fun GIVEN_message_at_length_limits_WHEN_rating_is_added_THEN_success() = runTest {
        assertEquals(AddRatingResult.Success, rate(message = "abc"))
        val otherPlayer = player(name = "Other", uuid = "0b3c6c1e-2f9b-3d17-9a5b-1d2a6f2b7c22")
        assertEquals(AddRatingResult.Success, rate(ratedPlayer = otherPlayer, message = "a".repeat(10)))
    }

    @Test
    fun GIVEN_daily_votes_can_not_be_counted_WHEN_rating_is_added_THEN_storage_failed() = runTest {
        repository.failingCalls += FakeRatingRepository.Call.COUNT_DAY_VOTES

        assertEquals(AddRatingResult.StorageFailed, rate())
    }

    @Test
    fun GIVEN_votes_on_player_can_not_be_counted_WHEN_rating_is_added_THEN_storage_failed() = runTest {
        repository.failingCalls += FakeRatingRepository.Call.COUNT_DAY_VOTES_ON_PLAYER

        assertEquals(AddRatingResult.StorageFailed, rate())
    }

    @Test
    fun GIVEN_rated_player_can_not_be_stored_WHEN_rating_is_added_THEN_storage_failed() = runTest {
        repository.failingCalls += FakeRatingRepository.Call.INSERT_USER

        assertEquals(AddRatingResult.StorageFailed, rate())
    }

    @Test
    fun GIVEN_only_creator_can_not_be_stored_WHEN_rating_is_added_THEN_storage_failed_and_nothing_is_rated() = runTest {
        storedUser(ratedPlayer)
        repository.failingCalls += FakeRatingRepository.Call.INSERT_USER

        assertEquals(AddRatingResult.StorageFailed, rate())
        assertEquals(emptyList(), repository.insertedRatings)
    }

    @Test
    fun GIVEN_rating_can_not_be_stored_WHEN_rating_is_added_THEN_storage_failed() = runTest {
        repository.failingCalls += FakeRatingRepository.Call.INSERT_RATING

        assertEquals(AddRatingResult.StorageFailed, rate())
    }
}
