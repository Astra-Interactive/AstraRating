import com.astrainteractive.astrarating.domain.api.IRatingAPI
import com.astrainteractive.astrarating.domain.api.RatingAPI
import com.astrainteractive.astrarating.domain.entities.User
import kotlinx.coroutines.runBlocking
import java.util.UUID
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class RatingAPITest {

    lateinit var api: IRatingAPI
    val database = Database()

    @BeforeTest
    fun setupDB() {
        runBlocking { database.onEnable() }
        api = RatingAPI(database)
    }

    val randomUser: User
        get() = User(
            minecraftUUID = UUID.randomUUID().toString(),
            minecraftName = UUID.randomUUID().toString().substring(0, 8),
        )

    @Test
    fun createUser() = runBlocking {
        val user = randomUser
        val inserted = api.insertUser(user)
        assertEquals(api.selectUser(user.minecraftName)?.id, inserted)
    }

    @Test
    fun updateUser() = runBlocking {
        val user = randomUser
        val id = api.insertUser(user)

    }
}

