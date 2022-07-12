package com.astrainteractive.astrarating.sqldatabase

import com.astrainteractive.astralibs.Logger
import com.astrainteractive.astralibs.catching
import com.astrainteractive.astrarating.AstraRating
import com.astrainteractive.astrarating.api.DatabaseApi
import com.astrainteractive.astrarating.utils.Translation
import java.io.File
import java.sql.DriverManager

object SQLDatabase : DatabaseCore() {
    private val _dbPath = "${AstraRating.instance.dataFolder}${File.separator}data.db"
    override suspend fun createConnection() {
        connection = catching { DriverManager.getConnection(("jdbc:sqlite:${_dbPath}")) }
    }

    override suspend fun onEnable() {
        createConnection()
        if (isInitialized)
            Logger.log(Translation.dbSuccess, "Database")
        else {
            Logger.error(Translation.dbFail, "Database")
            return
        }
        DatabaseApi.createUsersTable()
        DatabaseApi.createUsersRatingsTable()
    }
}