package com.astrainteractive.astrarating.sqldatabase

import com.astrainteractive.astralibs.catching
import com.astrainteractive.astralibs.mapNotNull
import com.astrainteractive.astrarating.api.DatabaseApi
import com.astrainteractive.astrarating.sqldatabase.entities.EntityInfo
import java.sql.Connection
import java.sql.ResultSet

abstract class DatabaseCore {
    var connection: Connection? = null
    val isInitialized: Boolean
        get() = connection?.isClosed != true

    companion object {
        fun sqlString(value: String) = "\"$value\""
        val String.sqlString: String
            get() = sqlString(this)
    }

    abstract suspend fun createConnection()
    abstract suspend fun onEnable()
    suspend fun close() = catching {
        connection?.close()
    }

    suspend fun createTable(table: String, entites: List<EntityInfo>) = catching(true) {
        val query = "CREATE TABLE IF NOT EXISTS $table (" + entites.joinToString(",") {
            mutableListOf<String>().apply {
                add("${it.name} ${it.type}")
                if (it.primaryKey) add("PRIMARY KEY")
                if (it.autoIncrement) add("AUTOINCREMENT")
                if (!it.nullable) add("NOT NULL")
                if (it.unique) add("UNIQUE")
            }.joinToString(" ")
        } + "); "
        return@catching connection?.prepareStatement(query)?.execute()
    }

    suspend fun insert(table: String, vararg entry: Pair<EntityInfo, Any>): Int? = catching(true) {
        val names = "(" + entry.map { it.first.name }.joinToString(",") + ") "
        val values = "VALUES(" + entry.map { it.second }.joinToString(",") + ");"
        val query = "INSERT INTO $table $names $values"
        return@catching connection?.prepareStatement(query)?.executeUpdate()
    }

    suspend fun <T, K> selectEntryByID(table: String, idName: String, id: K, builder: (ResultSet) -> T?): T? =
        catching(true) {
            val query = "SELECT * FROM $table WHERE $idName=$id"
            val rs = connection?.createStatement()?.executeQuery(query)
            return@catching rs?.mapNotNull { builder(it) }?.firstOrNull()
        }

    suspend fun <T> select(table: String, builder: (ResultSet) -> T): List<T>? = catching {
        val rs = connection?.createStatement()?.executeQuery("SELECT * FROM $table")
        return@catching rs?.mapNotNull { builder(it) }
    }

    suspend fun <T> deleteEntryByID(table: String, idName: String, id: T): Boolean? = catching(true) {
        val query = "DELETE FROM $table WHERE $idName=$id"
        return@catching connection?.prepareStatement(query)?.execute()
    }
}

