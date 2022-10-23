import com.astrainteractive.astrarating.domain.entities.User
import com.astrainteractive.astrarating.domain.entities.UserRating
import ru.astrainteractive.astralibs.database.DatabaseCore
import ru.astrainteractive.astralibs.database.isConnected
import java.sql.Connection
import java.sql.DriverManager

class Database : DatabaseCore() {
    override val connectionBuilder: () -> Connection? = {
        DriverManager.getConnection(("jdbc:sqlite:data.db"))
    }

    override suspend fun onEnable() {
        if (connection.isConnected)
            println("Success: ${connection}")
        else {
            throw Exception("Database not connected")
        }
        createTable<User>()
        createTable<UserRating>()
    }
}