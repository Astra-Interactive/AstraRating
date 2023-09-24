import kotlinx.coroutines.runBlocking
import ru.astrainteractive.astralibs.orm.Database
import ru.astrainteractive.astralibs.orm.exception.DatabaseException
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

abstract class ORMTest {
    abstract val builder: () -> Database
    protected var database: Database? = null
    fun assertConnected(): Database {
        return database ?: throw DatabaseException.DatabaseNotConnectedException
    }
    protected fun disableFullGroupBy() {
        val query = "SET sql_mode=(SELECT REPLACE(@@sql_mode,'ONLY_FULL_GROUP_BY',''));"
        database?.connection?.createStatement()?.executeUpdate(query)
    }

    @BeforeTest
    open fun setup(): Unit = runBlocking {
        database = builder()
        database?.openConnection()
//        disableFullGroupBy()
    }

    @AfterTest
    open fun destroy(): Unit = runBlocking {
        database?.closeConnection()
        database = null
    }
}
