package com.astrainteractive.astrarating.sqldatabase

import com.astrainteractive.astralibs.Logger
import com.astrainteractive.astralibs.database.DatabaseCore
import com.astrainteractive.astralibs.database.isConnected
import com.astrainteractive.astralibs.utils.catching
import com.astrainteractive.astrarating.AstraRating
import com.astrainteractive.astrarating.utils.Translation
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
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
        select<User>()?.filter { it.minecraftName!=it.minecraftName.uppercase() }?.map {
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
        instance = this
    }
}