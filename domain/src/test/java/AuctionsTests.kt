import kotlinx.coroutines.runBlocking
import com.astrainteractive.astrarating.domain.api.IRatingAPI
import com.astrainteractive.astrarating.domain.api.TableAPI
import com.astrainteractive.astrarating.domain.entities.tables.dto.UserDTO
import com.astrainteractive.astrarating.domain.entities.tables.dto.UserRatingDTO
import com.astrainteractive.astrarating.domain.entities.tables.UserRatingTable
import com.astrainteractive.astrarating.domain.entities.tables.UserTable
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import ru.astrainteractive.astralibs.orm.DBConnection
import ru.astrainteractive.astralibs.orm.Database
import java.io.File
import java.util.*
import kotlin.random.Random
import kotlin.test.DefaultAsserter.assertEquals
import kotlin.test.DefaultAsserter.assertNotNull
import kotlin.test.assertEquals
import kotlin.test.*

class AuctionsTests {
    private lateinit var databaseV2: Database
    private lateinit var api: IRatingAPI

    val randomUser: UserDTO
        get() = UserDTO(
            minecraftUUID = UUID.randomUUID().toString(),
            minecraftName = UUID.randomUUID().toString(),
            discordID = UUID.randomUUID().toString(),
        )

    fun userRating(
        userCreatedReport: Long,
        reportedUser: Long,
    ): UserRatingDTO = UserRatingDTO(
        userCreatedReport = userCreatedReport,
        reportedUser = reportedUser,
        rating = Random.nextInt(-10, 10),
        message = UUID.randomUUID().toString()
    )

    @BeforeEach
    fun setup(): Unit = runBlocking {
        val dbName = "dbv2_auction.db"
        File(dbName).delete()
        databaseV2 = Database()
        databaseV2.openConnection("$dbName", DBConnection.SQLite)
        UserTable.create()
        UserRatingTable.create()
        api = TableAPI(databaseV2)
    }

    @AfterEach
    fun destruct(): Unit = runBlocking {
        databaseV2.closeConnection()
    }

    @Test
    fun `Insert, fetch, expire same auction`(): Unit = runBlocking {
        val user = randomUser
        // Insert and select user
        api.insertUser(user)
        var userCreatedReport = api.selectUser(user.minecraftName)
        assertNotNull(userCreatedReport)
        assertEquals(user.minecraftUUID, userCreatedReport?.minecraftUUID)
        // Insert user rating
        var reportedUser = randomUser
        reportedUser = reportedUser.copy(id = api.insertUser(reportedUser)!!)
        // Insert user rating
        var userRating = userRating(userCreatedReport.id, reportedUser.id)
        userRating = userRating.copy(id = api.insertUserRating(userRating)!!)
        val reportsAmount = api.fetchUserRatings(reportedUser.minecraftName)?.size!!
        assertEquals(reportsAmount, 1)
        val countPlayerRated = api.countPlayerTotalDayRated(user.minecraftName)
        assertEquals(countPlayerRated, 1)
        val playerOnPlayerCount = api.countPlayerOnPlayerDayRated(user.minecraftName, reportedUser.minecraftName)
        assertEquals(playerOnPlayerCount, 1)
    }

}