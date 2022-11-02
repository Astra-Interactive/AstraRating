package com.astrainteractive.astrarating.domain

import ru.astrainteractive.astralibs.Logger
import ru.astrainteractive.astralibs.database.DatabaseCore
import ru.astrainteractive.astralibs.database.isConnected
import ru.astrainteractive.astralibs.utils.catching
import com.astrainteractive.astrarating.domain.entities.User
import com.astrainteractive.astrarating.domain.entities.UserRating
import kotlinx.coroutines.*
import java.sql.Connection
import java.sql.DriverManager


class SQLDatabase(filePath: String ) : DatabaseCore() {

    override val connectionBuilder: () -> Connection? = {
        catching { DriverManager.getConnection(("jdbc:sqlite:${filePath}")) }
    }

    override suspend fun onEnable() {
        if (connection.isConnected)
            Logger.log("Database connected", "Database")
        else {
            Logger.error("Database error", "Database")
            return
        }
        createTable<User>()
        createTable<UserRating>()
        select<User>()?.filter { it.minecraftName != it.minecraftName.uppercase() }?.map {
            coroutineScope {
                async {
                    update(it.copy(minecraftName = it.minecraftName.uppercase()))
                }
            }
        }?.awaitAll()
    }

    companion object {
        const val NON_EXISTS_KEY = -1L
    }
}