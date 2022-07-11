package com.astrainteractive.astrarating.sqldatabase

import com.astrainteractive.astralibs.Logger
import com.astrainteractive.astralibs.catching
import com.astrainteractive.astrarating.AstraRating
import com.astrainteractive.astrarating.api.DatabaseApi
import com.astrainteractive.astrarating.utils.Translation
import kotlinx.coroutines.runBlocking
import java.io.File
import java.sql.Connection
import java.sql.DriverManager


/**
 * Database for plugin
 */
class Database(folder: String = "${AstraRating.instance.dataFolder}${File.separator}data.db") {
    /**
     * Path for your plugin database
     */
    private val _dbPath = folder

    /**
     * Connection for your database.
     */
    companion object {
        lateinit var connection: Connection
        val isInitialized: Boolean
            get() = this::connection.isInitialized && !connection.isClosed
    }

    /**
     * Function for connecting to database
     */
    private suspend fun connectDatabase() =
        catching {
            connection = DriverManager.getConnection(("jdbc:sqlite:${_dbPath}"))
            return@catching isInitialized
        }


    /**
     * Here we launch our tasks async
     *
     * Also the [Callback] implementation
     */
    suspend fun onEnable() {
            connectDatabase()
            if (isInitialized)
                Logger.log(Translation.dbSuccess, "Database")
            else {
                Logger.error(Translation.dbFail, "Database")
                return
            }
        runBlocking {
            DatabaseApi.createUsersTable()
            DatabaseApi.createUsersRatingsTable()
        }
    }


    public suspend fun onDisable() {
        connection.close()
    }


}


