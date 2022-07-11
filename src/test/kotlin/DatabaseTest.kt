import com.astrainteractive.astrarating.sqldatabase.Database
import kotlinx.coroutines.runBlocking
import org.junit.Test

class DatabaseTest() {
    @Test
    fun test() {
        val database = Database("data.db")
        runBlocking { database.onEnable() }
    }
}