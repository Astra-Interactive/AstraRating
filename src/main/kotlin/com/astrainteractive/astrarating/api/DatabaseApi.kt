package com.astrainteractive.astrarating.api

import com.astrainteractive.astralibs.Logger
import com.astrainteractive.astralibs.catching
import com.astrainteractive.astralibs.mapNotNull
import com.astrainteractive.astrarating.sqldatabase.Database
import com.astrainteractive.astrarating.sqldatabase.entities.EntityInfo
import com.astrainteractive.astrarating.sqldatabase.entities.User
import com.astrainteractive.astrarating.sqldatabase.entities.UserRating
import java.sql.Connection
import java.sql.ResultSet


/**
 * API with all SQL commands
 */
object DatabaseApi {
    private val connection: Connection
        get() = Database.connection
    const val NON_EXISTS_KEY = 0L
    private val TAG = "DatabaseApi"
    private suspend fun createTable(table: String, entites: List<EntityInfo>) = catching(true) {
        val query = "CREATE TABLE IF NOT EXISTS $table (" + entites.joinToString(",") {
            mutableListOf<String>().apply {
                add("${it.name} ${it.type}")
                if (it.primaryKey) add("PRIMARY KEY")
                if (it.autoIncrement) add("AUTOINCREMENT")
                if (!it.nullable) add("NOT NULL")
                if (it.unique) add("UNIQUE")
            }.joinToString(" ")
        } + "); "
        Logger.log("createTable query: $query", TAG)
        return@catching connection.prepareStatement(query).execute()
    }

    suspend fun createUsersTable() = catching {
        return@catching createTable(User.TABLE, User.entities)
    }

    suspend fun createUsersRatingsTable() = catching {
        return@catching createTable(UserRating.TABLE, UserRating.entities)
    }

    fun sqlString(value: String) = "\"$value\""
    suspend fun insertUserTable(it: User): Int? {
        return insert(
            User.TABLE,
            User.minecraftUUID to sqlString(it.minecraftUUID),
            User.name to sqlString(it.minecraftName),
            User.discordID to sqlString(it.discordID),
            User.lastUpdated to it.lastUpdated
        )
    }

    suspend fun insertUserRating(it: UserRating) {
        insert(
            UserRating.TABLE,
            UserRating.userCreatedReport to it.userCreatedReport,
            UserRating.reportedUser to it.reportedUser,
            UserRating.rating to it.rating,
            UserRating.message to sqlString(it.message),
            UserRating.time to it.time
        )
    }

    private suspend fun insert(table: String, vararg entry: Pair<EntityInfo, Any>): Int? = catching(true) {
        val names = "(" + entry.map { it.first.name }.joinToString(",") + ") "
        val values = "VALUES(" + entry.map { it.second }.joinToString(",") + ");"
        val query = "INSERT INTO $table $names $values"
        return@catching connection.prepareStatement(query).executeUpdate()
    }

    suspend fun <T, K> selectEntryByID(table: String, idName: String, id: K, builder: (ResultSet) -> T?): T? =
        catching(true) {
            val query = "SELECT * FROM $table WHERE $idName=$id"
            val rs = connection.createStatement().executeQuery(query)
            return@catching rs.mapNotNull { builder(it) }.firstOrNull()
        }

    suspend fun <T> select(table: String, builder: (ResultSet) -> T): List<T>? = catching {
        val rs = connection.createStatement().executeQuery("SELECT * FROM $table")
        return@catching rs.mapNotNull { builder(it) }
    }

    suspend fun deleteEntryByID(table: String, idName: String, id: Int): Boolean? = catching {
        val query = "DELETE FROM $table WHERE $idName=$id"
        return@catching connection.prepareStatement(query).execute()
    }
}
