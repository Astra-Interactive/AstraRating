import com.astrainteractive.astrarating.domain.api.IRatingAPI
import com.astrainteractive.astrarating.domain.api.TableAPI
import com.astrainteractive.astrarating.domain.entities.tables.UserRatingTable
import com.astrainteractive.astrarating.domain.entities.tables.UserTable
import com.astrainteractive.astrarating.domain.entities.tables.dto.UserDTO
import com.astrainteractive.astrarating.domain.entities.tables.dto.UserRatingDTO
import kotlinx.coroutines.runBlocking
import ru.astrainteractive.astralibs.orm.DBConnection
import ru.astrainteractive.astralibs.orm.DBSyntax
import ru.astrainteractive.astralibs.orm.Database
import ru.astrainteractive.astralibs.orm.DefaultDatabase
import java.io.File
import java.util.*
import kotlin.random.Random
import kotlin.test.*

class AuctionsTests : ORMTest() {
    private lateinit var api: IRatingAPI
    private val dbName = "dbv2_auction.db"

//    override val builder: () -> Database
//        get() = { DefaultDatabase(DBConnection.SQLite(dbName), DBSyntax.SQLite) }
    override val builder: () -> Database
        get() = {
            val dbconnection = DBConnection.MySQL(
                database = "XXXXXXXXX",
                ip = "XXXXXX",
                port = 3306,
                username = "XXXXXXXXXX",
                password = "XXXXXXXXXXXXX"
            )
            DefaultDatabase(dbconnection, DBSyntax.MySQL)
        }

    val randomUser: UserDTO
        get() = UserDTO(
            minecraftUUID = UUID.randomUUID().toString(),
            minecraftName = UUID.randomUUID().toString(),
            discordID = UUID.randomUUID().toString(),
        )

    fun userRating(
        userCreatedReport: Int,
        reportedUser: Int,
    ): UserRatingDTO = UserRatingDTO(
        userCreatedReport = userCreatedReport,
        reportedUser = reportedUser,
        rating = 1,
        message = UUID.randomUUID().toString()
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
        api = TableAPI(database)
    }


    @Test
    fun `Insert and select`(): Unit = runBlocking {
        val database = assertConnected()
        val user = randomUser
        // Insert and select user
        val id = api.insertUser(user)
        api.selectUser(user.minecraftName).also { selectedUser ->
            assertNotNull(selectedUser)
            assertEquals(id, selectedUser.id)
            assertEquals(user.minecraftUUID, selectedUser.minecraftUUID)
        }
    }

    @Test
    fun `Rate user on user`(): Unit = runBlocking {
        val reportedUser = randomUser.let {
            api.insertUser(it)
            assertNotNull(api.selectUser(it.minecraftName))
        }
        val userCreatedReport = randomUser.let {
            api.insertUser(it)
            assertNotNull(api.selectUser(it.minecraftName))
        }
        userRating(userCreatedReport.id, reportedUser.id).also {
            assertNotNull(api.insertUserRating(it))
        }
        api.fetchUserRatings(reportedUser.minecraftName).also { reportsOnUser ->
            assertNotNull(reportsOnUser)
            assertEquals(1, reportsOnUser.size)
        }
        api.countPlayerOnPlayerDayRated(userCreatedReport.minecraftName, reportedUser.minecraftName).also { count ->
            assertNotNull(count)
            assertEquals(1, count)
        }
        api.countPlayerTotalDayRated(userCreatedReport.minecraftName).also { count ->
            assertNotNull(count)
            assertEquals(1, count)
        }
        api.fetchUsersTotalRating().also { ratings ->
            assertNotNull(ratings)
            assertEquals(1, ratings.size)
        }
        userRating(userCreatedReport.id, reportedUser.id).also {
            assertNotNull(api.insertUserRating(it))
        }
        api.fetchUserRatings(reportedUser.minecraftName).also { userRatings ->
            assertNotNull(userRatings)
            assertEquals(2, userRatings.size)
        }
        api.fetchUsersTotalRating().also { ratings ->
            assertNotNull(ratings)
            val rating = assertNotNull(ratings.firstOrNull())

            assertEquals(2, rating.rating.rating)
        }
    }
}