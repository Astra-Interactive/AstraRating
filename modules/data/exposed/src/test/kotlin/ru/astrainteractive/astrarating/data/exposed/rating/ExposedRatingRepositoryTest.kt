@file:Suppress("FunctionNaming")

package ru.astrainteractive.astrarating.data.exposed.rating

import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import ru.astrainteractive.astralibs.util.YamlStringFormat
import ru.astrainteractive.astrarating.data.dao.rating.model.NewUser
import ru.astrainteractive.astrarating.data.dao.rating.model.RatingStorageError
import ru.astrainteractive.astrarating.data.dao.rating.model.RatingType
import ru.astrainteractive.astrarating.data.dao.rating.model.User
import ru.astrainteractive.astrarating.data.dao.rating.model.UserRating
import ru.astrainteractive.astrarating.data.exposed.di.ExposedModule
import ru.astrainteractive.astrarating.data.exposed.model.DbRatingConfiguration
import ru.astrainteractive.astrarating.data.exposed.table.UserRatingTable
import ru.astrainteractive.klibs.mikro.exposed.model.DatabaseConfiguration
import java.nio.file.Files
import java.util.UUID
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.time.Clock

class ExposedRatingRepositoryTest {
    private var module: ExposedModule? = null
    private val requireModule: ExposedModule
        get() = module ?: error("The module is null")

    private val repository: ExposedRatingRepository
        get() = ExposedRatingRepository(
            databaseFlow = requireModule.databaseFlow,
            clock = Clock.System,
            isDebugProvider = { false }
        )

    private val randomUser: NewUser
        get() = NewUser(
            minecraftUUID = UUID.randomUUID(),
            minecraftName = UUID.randomUUID().toString(),
        )

    private suspend fun insertRandomUser(): User = repository.insertUser(randomUser).getOrThrow()

    private suspend fun rate(
        reporter: User?,
        reported: User,
        ratingValue: Int,
        type: RatingType = RatingType.USER_RATING
    ) {
        repository.insertUserRating(
            reporter = reporter,
            reported = reported,
            message = "message $ratingValue",
            type = type,
            ratingValue = ratingValue
        ).getOrThrow()
    }

    private fun User.uuid(): UUID = UUID.fromString(minecraftUUID)

    @AfterTest
    fun destroy() {
        requireModule.lifecycle.onDisable()
    }

    @BeforeTest
    fun setup() {
        runTest {
            val folder = Files.createTempDirectory("dir").toFile()
            module = ExposedModule(
                stringFormat = YamlStringFormat(),
                defaultConfig = {
                    val configuration = folder
                        .resolve("dbfile")
                        .absolutePath
                        .let(DatabaseConfiguration::SQLite)
                    DbRatingConfiguration(databaseConfiguration = configuration)
                },
                dataFolder = folder
            )
        }
    }

    @Test
    fun GIVEN_inserted_user_WHEN_user_is_found_THEN_stored_user_is_returned() {
        runTest {
            val inserted = insertRandomUser()

            val found = repository.findUser(inserted.uuid()).getOrThrow()

            assertEquals(inserted, found)
        }
    }

    @Test
    fun GIVEN_unknown_player_WHEN_user_is_found_THEN_returns_null() {
        runTest {
            val found = repository.findUser(UUID.randomUUID()).getOrThrow()

            assertNull(found)
        }
    }

    @Test
    fun GIVEN_renamed_user_WHEN_user_is_updated_THEN_new_name_is_stored() {
        runTest {
            val inserted = insertRandomUser()

            repository.updateUser(inserted.copy(minecraftName = "Renamed")).getOrThrow()

            assertEquals("Renamed", repository.findUser(inserted.uuid()).getOrThrow()?.minecraftName)
        }
    }

    @Test
    fun GIVEN_two_ratings_on_player_WHEN_ratings_are_fetched_THEN_both_are_listed_and_counted() {
        runTest {
            val reported = insertRandomUser()
            val reporter = insertRandomUser()

            rate(
                reporter = reporter,
                reported = reported,
                ratingValue = 1
            )
            rate(
                reporter = reporter,
                reported = reported,
                ratingValue = 1
            )

            assertEquals(2, repository.fetchUserRatings(reported.uuid()).getOrThrow().size)
            assertEquals(2, repository.countPlayerOnPlayerDayRated(reporter.uuid(), reported.uuid()).getOrThrow())
            assertEquals(2, repository.countPlayerTotalDayRated(reporter.uuid()).getOrThrow())
            val ratedUser = repository.fetchUsersTotalRating().getOrThrow().single()
            assertEquals(2, ratedUser.ratingTotal)
            assertEquals(2, ratedUser.ratingCounts)
        }
    }

    @Test
    fun GIVEN_unknown_player_WHEN_ratings_are_fetched_or_counted_THEN_nothing_is_found() {
        runTest {
            val known = insertRandomUser()
            val unknown = UUID.randomUUID()

            assertEquals(emptyList(), repository.fetchUserRatings(unknown).getOrThrow())
            assertEquals(0, repository.countPlayerTotalDayRated(unknown).getOrThrow())
            assertEquals(0, repository.countPlayerOnPlayerDayRated(unknown, known.uuid()).getOrThrow())
            assertEquals(0, repository.countPlayerOnPlayerDayRated(known.uuid(), unknown).getOrThrow())
        }
    }

    @Test
    fun GIVEN_ratings_from_two_reporters_WHEN_one_fetched_rating_is_deleted_THEN_only_it_is_removed() {
        runTest {
            val reported = insertRandomUser()
            rate(
                reporter = insertRandomUser(),
                reported = reported,
                ratingValue = 1
            )
            rate(
                reporter = insertRandomUser(),
                reported = reported,
                ratingValue = -1
            )

            val firstRating = repository.fetchUserRatings(reported.uuid())
                .getOrThrow()
                .single { rating -> rating.rating == 1 }
            repository.deleteUserRating(firstRating).getOrThrow()

            val remainingRatings = repository.fetchUserRatings(reported.uuid())
                .getOrThrow()
                .map(UserRating::rating)
            assertEquals(listOf(-1), remainingRatings)
        }
    }

    @Test
    fun GIVEN_kill_rating_WHEN_player_ratings_are_fetched_THEN_it_is_listed_without_reporter() {
        runTest {
            val killer = insertRandomUser()
            val reporter = insertRandomUser()
            rate(
                reporter = reporter,
                reported = killer,
                ratingValue = 1
            )
            rate(
                reporter = null,
                reported = killer,
                ratingValue = -1,
                type = RatingType.PLAYER_KILL
            )

            val ratings = repository.fetchUserRatings(killer.uuid()).getOrThrow()

            val killRating = ratings.single { rating -> rating.ratingType == RatingType.PLAYER_KILL }
            val userRating = ratings.single { rating -> rating.ratingType == RatingType.USER_RATING }
            assertNull(killRating.userCreatedReport)
            assertEquals(reporter.id, userRating.userCreatedReport?.id)
        }
    }

    @Test
    fun GIVEN_user_and_kill_ratings_WHEN_total_is_fetched_THEN_both_are_summed() {
        runTest {
            val killer = insertRandomUser()
            rate(
                reporter = insertRandomUser(),
                reported = killer,
                ratingValue = 3
            )
            rate(
                reporter = null,
                reported = killer,
                ratingValue = -1,
                type = RatingType.PLAYER_KILL
            )

            val total = repository.fetchUserTotalRating(killer.uuid()).getOrThrow()

            assertEquals(2, total)
        }
    }

    @Test
    fun GIVEN_empty_database_WHEN_total_is_fetched_THEN_returns_zero() {
        runTest {
            val total = repository.fetchUserTotalRating(UUID.randomUUID()).getOrThrow()

            assertEquals(0, total)
        }
    }

    @Test
    fun GIVEN_broken_table_WHEN_total_is_fetched_THEN_query_failure_is_returned() {
        runTest {
            val database = requireModule.databaseFlow.first()
            transaction(database) { SchemaUtils.drop(UserRatingTable) }

            val result = repository.fetchUserTotalRating(UUID.randomUUID())

            assertIs<RatingStorageError.QueryFailed>(result.exceptionOrNull())
        }
    }

    @Test
    fun GIVEN_database_never_connects_WHEN_user_is_found_THEN_database_unavailable_is_returned() {
        runTest {
            val disconnectedRepository = ExposedRatingRepository(
                databaseFlow = flow<Database> { awaitCancellation() },
                clock = Clock.System,
                isDebugProvider = { true }
            )

            val result = disconnectedRepository.findUser(UUID.randomUUID())

            assertIs<RatingStorageError.DatabaseUnavailable>(result.exceptionOrNull())
        }
    }
}
