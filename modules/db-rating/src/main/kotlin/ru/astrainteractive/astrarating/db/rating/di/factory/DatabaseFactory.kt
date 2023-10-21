package ru.astrainteractive.astrarating.db.rating.di.factory

import org.jetbrains.exposed.sql.Database
import ru.astrainteractive.astrarating.db.rating.model.DBConnection
import ru.astrainteractive.klibs.kdi.Factory

class DatabaseFactory(
    private val dbConnection: DBConnection
) : Factory<Database> {
    override fun create(): Database {
        return when (dbConnection) {
            is DBConnection.MySql -> {
                Database.connect(
                    url = dbConnection.url,
                    driver = dbConnection.driver,
                    user = dbConnection.user,
                    password = dbConnection.password
                )
            }

            is DBConnection.SQLite -> {
                Database.connect(
                    url = dbConnection.url,
                    driver = dbConnection.driver
                )
            }
        }
    }
}
