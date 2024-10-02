package ru.astrainteractive.astrarating.db.rating.di.factory

import org.jetbrains.exposed.sql.Database
import ru.astrainteractive.astrarating.db.rating.model.DBConnection

class DatabaseFactory(
    private val dbConnection: DBConnection
) {
    fun create(): Database {
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
