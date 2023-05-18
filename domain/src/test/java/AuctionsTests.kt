import com.astrainteractive.astrarating.domain.api.RatingDBApi
import com.astrainteractive.astrarating.domain.api.impl.RatingDBApiImpl
import com.astrainteractive.astrarating.domain.entities.UserRatingTable
import com.astrainteractive.astrarating.domain.entities.UserTable
import com.astrainteractive.astrarating.dto.RatingType
import com.astrainteractive.astrarating.models.UserModel
import kotlinx.coroutines.runBlocking
import ru.astrainteractive.astralibs.orm.Database
import java.io.File
import java.util.UUID
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class AuctionsTests : ORMTest() {
    private lateinit var api: RatingDBApi
    private val dbName = "dbv2_auction.db"

    override val builder: () -> Database = Resource::getDatabase

    val randomUser: UserModel
        get() = UserModel(
            minecraftUUID = UUID.randomUUID(),
            minecraftName = UUID.randomUUID().toString(),
            discordID = UUID.randomUUID().toString(),
        )

    @AfterTest
    override fun destroy(): Unit = runBlocking {
        val database = assertConnected()
        UserTable.drop(database)
        UserRatingTable.drop(database)
        super.destroy()
    }

    @BeforeTest
    override fun setup(): Unit = runBlocking {
        super.setup()
        val database = assertConnected()
        File(dbName).delete()
        database.openConnection()
        UserTable.create(database)
        UserRatingTable.create(database)
        api = RatingDBApiImpl(database, File("."))
    }

    @Test
    fun `Insert and select`(): Unit = runBlocking {
        val database = assertConnected()
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

            assertEquals(2, rating.rating.rating)
        }
    }
}
