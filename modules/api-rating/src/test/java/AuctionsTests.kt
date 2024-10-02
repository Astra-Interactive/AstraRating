import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import ru.astrainteractive.astrarating.api.rating.api.RatingDBApi
import ru.astrainteractive.astrarating.api.rating.di.ApiRatingModule
import ru.astrainteractive.astrarating.db.rating.di.factory.RatingDatabaseFactory
import ru.astrainteractive.astrarating.db.rating.model.DBConnection
import ru.astrainteractive.astrarating.dto.RatingType
import ru.astrainteractive.astrarating.model.UserModel
import ru.astrainteractive.klibs.kstorage.api.impl.DefaultMutableKrate
import java.io.File
import java.util.UUID
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class AuctionsTests {

    private val dbName = "./dbv2_auction.db"

    private val database = DefaultMutableKrate(
        factory = { null },
        loader = {
            RatingDatabaseFactory(DBConnection.SQLite(dbName)).create()
        }
    )

    private val api: RatingDBApi
        get() = ApiRatingModule.Default(
            databaseFlow = flowOf(database.cachedValue).filterNotNull(),
            coroutineScope = GlobalScope,
            isDebugProvider = { false }
        ).ratingDBApi

    val randomUser: UserModel
        get() = UserModel(
            minecraftUUID = UUID.randomUUID(),
            minecraftName = UUID.randomUUID().toString(),
            discordID = UUID.randomUUID().toString(),
        )

    @AfterTest
    fun destroy(): Unit = runBlocking {
        database.cachedValue?.connector?.invoke()?.close()
    }

    @BeforeTest
    fun setup(): Unit = runBlocking {
        File("./$dbName").delete()
        database.cachedValue?.connector?.invoke()?.close()
        database?.loadAndGet()
        database.cachedValue
    }

    @Test
    fun `Insert and select`(): Unit = runBlocking {
        val user = randomUser
        // Insert and select user
        val id = api.insertUser(user).getOrThrow()
        api.selectUser(user.minecraftName).getOrThrow().also { selectedUser ->
            assertNotNull(selectedUser)
            assertEquals(id, selectedUser.id)
            assertEquals(user.minecraftUUID.toString(), selectedUser.minecraftUUID)
        }
    }

    @Test
    fun `Rate user on user`(): Unit = runBlocking {
        val reportedUser = randomUser.let {
            api.insertUser(it).getOrThrow()
            api.selectUser(it.minecraftName).getOrThrow()
        }
        val userCreatedReport = randomUser.let {
            api.insertUser(it).getOrThrow()
            api.selectUser(it.minecraftName).getOrThrow()
        }
        api.insertUserRating(
            reporter = userCreatedReport,
            reported = reportedUser,
            message = "",
            type = RatingType.USER_RATING,
            ratingValue = 1
        ).getOrThrow()
        api.fetchUserRatings(reportedUser.minecraftName).getOrThrow().also { reportsOnUser ->
            assertNotNull(reportsOnUser)
            assertEquals(1, reportsOnUser.size)
        }
        api.countPlayerOnPlayerDayRated(userCreatedReport.minecraftName, reportedUser.minecraftName).getOrThrow()
            .also { count ->
                assertNotNull(count)
                assertEquals(1, count)
            }
        api.countPlayerTotalDayRated(userCreatedReport.minecraftName).getOrThrow().also { count ->
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
        api.fetchUserRatings(reportedUser.minecraftName).getOrThrow().also { userRatings ->
            assertNotNull(userRatings)
            assertEquals(2, userRatings.size)
        }
        api.fetchUsersTotalRating().getOrThrow().also { ratings ->
            assertNotNull(ratings)
            val rating = assertNotNull(ratings.firstOrNull())

            assertEquals(2, rating.rating)
        }
    }
}
