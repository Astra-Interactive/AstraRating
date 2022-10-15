package com.astrainteractive.astrarating.sqldatabase

import ru.astrainteractive.astralibs.Logger
import ru.astrainteractive.astralibs.database.DatabaseCore
import ru.astrainteractive.astralibs.database.isConnected
import ru.astrainteractive.astralibs.utils.catching
import com.astrainteractive.astrarating.AstraRating
import com.astrainteractive.astrarating.utils.Translation
import kotlinx.coroutines.*
import java.io.File
import java.sql.Connection
import java.sql.DriverManager


class SQLDatabase(filePath: String = "${AstraRating.instance.dataFolder}${File.separator}data.db") : DatabaseCore() {

    override val connectionBuilder: () -> Connection? = {
        catching { DriverManager.getConnection(("jdbc:sqlite:${filePath}")) }
    }

    override suspend fun onEnable() {
        if (connection.isConnected)
            Logger.log(Translation.dbSuccess, "Database")
        else {
            Logger.error(Translation.dbFail, "Database")
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
        var instance: SQLDatabase? = null
            private set
    }

    init {
        runBlocking(Dispatchers.IO) {
            val connection = instance?.connection?.apply { close() }
            while (connection?.isClosed == false) {
                Logger.warn("Waiting previous database to close: $connection; ${connection?.isClosed}")

                delay(500)
            }
            instance = this@SQLDatabase
        }
    }
}