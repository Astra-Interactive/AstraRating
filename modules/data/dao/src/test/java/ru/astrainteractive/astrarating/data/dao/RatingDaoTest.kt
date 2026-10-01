@file:Suppress("FunctionNaming")

package ru.astrainteractive.astrarating.data.dao

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager
import ru.astrainteractive.astralibs.util.YamlStringFormat
import ru.astrainteractive.astrarating.data.dao.di.RatingDaoModule
import ru.astrainteractive.astrarating.data.exposed.db.rating.di.DBRatingModule
import ru.astrainteractive.astrarating.data.exposed.db.rating.model.DbRatingConfiguration
import ru.astrainteractive.astrarating.data.exposed.dto.RatingType
import ru.astrainteractive.astrarating.data.exposed.dto.UserDTO
import ru.astrainteractive.astrarating.data.exposed.model.UserModel
import ru.astrainteractive.klibs.mikro.exposed.model.DatabaseConfiguration
import java.nio.file.Files
import java.util.UUID
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class RatingDaoTest {

    private var module: DBRatingModule? = null
    private val requireModule: DBRatingModule
        get() = module ?: error("The module is null")

    private val api: RatingDao
        get() = RatingDaoModule(
            databaseFlow = requireModule.databaseFlow,
            coroutineScope = GlobalScope,
            isDebugProvider = { false }
        ).ratingDao

    val randomUser: UserModel
        get() = UserModel(
            minecraftUUID = UUID.randomUUID(),
            minecraftName = UUID.randomUUID().toString(),
        )

    private fun getTempFolder() = Files.createTempDirectory("dir").toFile()

    private suspend fun insertRandomUser(): UserDTO {
        val user = randomUser
        api.insertUser(user).getOrThrow()
        return api.selectUser(user.minecraftUUID).getOrThrow()
    }

    @AfterTest
    fun destroy(): Unit = runBlocking {
        TransactionManager.closeAndUnregister(requireModule.databaseFlow.first())
    }

    @BeforeTest
    fun setup(): Unit = runBlocking {
        val folder = getTempFolder()
        module = DBRatingModule(
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

    @Test
    fun `Insert and select`(): Unit = runBlocking {
        val user = randomUser
        // Insert and select user
        val id = api.insertUser(user).getOrThrow()
        api.selectUser(user.minecraftUUID).getOrThrow().also { selectedUser ->
            assertNotNull(selectedUser)
            assertEquals(id, selectedUser.id)
            assertEquals(user.minecraftUUID.toString(), selectedUser.minecraftUUID)
        }
    }

    @Test
    fun `Rate user on user`(): Unit = runBlocking {
        val reportedUser = randomUser.let {
            api.insertUser(it).getOrThrow()
            api.selectUser(it.minecraftUUID).getOrThrow()
        }
        val userCreatedReport = randomUser.let {
            api.insertUser(it).getOrThrow()
            api.selectUser(it.minecraftUUID).getOrThrow()
        }
        api.insertUserRating(
            reporter = userCreatedReport,
            reported = reportedUser,
            message = "",
            type = RatingType.USER_RATING,
            ratingValue = 1
        ).getOrThrow()
        api.fetchUserRatings(reportedUser.minecraftUUID.let(UUID::fromString)).getOrThrow().also { reportsOnUser ->
            assertNotNull(reportsOnUser)
            assertEquals(1, reportsOnUser.size)
        }
        api.countPlayerOnPlayerDayRated(
            userCreatedReport.minecraftUUID.let(UUID::fromString),
            reportedUser.minecraftUUID.let(UUID::fromString)
        ).getOrThrow()
            .also { count ->
                assertNotNull(count)
                assertEquals(1, count)
            }
        api.countPlayerTotalDayRated(userCreatedReport.minecraftUUID.let(UUID::fromString)).getOrThrow().also { count ->
            assertNotNull(count)
            assertEquals(1, count)
        }
        api.fetchUsersTotalRating().getOrThrow().also { ratings ->
            assertNotNull(ratings)
            assertEquals(1, ratings.size)
        }
        api.insertUserRating(
            reporter = userCreatedReport,
            reported = reportedUser,
            message = "",
            type = RatingType.USER_RATING,
            ratingValue = 1
        ).getOrThrow()
        api.fetchUserRatings(reportedUser.minecraftUUID.let(UUID::fromString)).getOrThrow().also { userRatings ->
            assertNotNull(userRatings)
            assertEquals(2, userRatings.size)
        }
        api.fetchUsersTotalRating().getOrThrow().also { ratings ->
            assertNotNull(ratings)
            val rating = assertNotNull(ratings.firstOrNull())

            assertEquals(2, rating.ratingTotal)
        }
    }

    @Test
    fun GIVEN_rating_without_reporter_WHEN_total_ratings_are_fetched_THEN_it_is_counted(): Unit = runBlocking {
        val killer = insertRandomUser()
        val reporter = insertRandomUser()
        api.insertUserRating(
            reporter = reporter,
            reported = killer,
            message = "",
            type = RatingType.USER_RATING,
            ratingValue = 5
        ).getOrThrow()
        api.insertUserRating(
            reporter = null,
            reported = killer,
            message = "",
            type = RatingType.PLAYER_KILL,
            ratingValue = -2
        ).getOrThrow()

        val killerRating = api.fetchUsersTotalRating()
            .getOrThrow()
            .single { ratedUser -> ratedUser.userDTO.id == killer.id }

        assertEquals(3, killerRating.ratingTotal)
    }

    @Test
    fun GIVEN_ratings_from_two_reporters_WHEN_one_fetched_rating_is_deleted_THEN_only_it_is_removed(): Unit =
        runBlocking {
            val reportedUser = insertRandomUser()
            api.insertUserRating(
                reporter = insertRandomUser(),
                reported = reportedUser,
                message = "first",
                type = RatingType.USER_RATING,
                ratingValue = 1
            ).getOrThrow()
            api.insertUserRating(
                reporter = insertRandomUser(),
                reported = reportedUser,
                message = "second",
                type = RatingType.USER_RATING,
                ratingValue = -1
            ).getOrThrow()
            val reportedUuid = UUID.fromString(reportedUser.minecraftUUID)

            val firstRating = api.fetchUserRatings(reportedUuid)
                .getOrThrow()
                .single { rating -> rating.message == "first" }
            api.deleteUserRating(firstRating).getOrThrow()

            val remainingMessages = api.fetchUserRatings(reportedUuid)
                .getOrThrow()
                .map { rating -> rating.message }
            assertEquals(listOf("second"), remainingMessages)
        }
}
