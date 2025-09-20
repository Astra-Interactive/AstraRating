package ru.astrainteractive.astrarating.data.dao

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.transactions.TransactionManager
import ru.astrainteractive.astralibs.exposed.model.DatabaseConfiguration
import ru.astrainteractive.astralibs.util.YamlStringFormat
import ru.astrainteractive.astrarating.data.dao.di.RatingDaoModule
import ru.astrainteractive.astrarating.data.exposed.db.rating.di.DBRatingModule
import ru.astrainteractive.astrarating.data.exposed.db.rating.model.DbRatingConfiguration
import ru.astrainteractive.astrarating.data.exposed.dto.RatingType
import ru.astrainteractive.astrarating.data.exposed.model.UserModel
import java.io.File
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

    @AfterTest
    fun destroy(): Unit = runBlocking {
        TransactionManager.Companion.closeAndUnregister(requireModule.databaseFlow.first())
        File("./test").deleteRecursively()
    }

    @BeforeTest
    fun setup(): Unit = runBlocking {
        module = DBRatingModule(
            stringFormat = YamlStringFormat(),
            defaultConfig = {
                DbRatingConfiguration(databaseConfiguration = DatabaseConfiguration.SQLite("test"))
            },
            dataFolder = File("./test").also {
                it.mkdirs()
                it.deleteOnExit()
            }
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
}
