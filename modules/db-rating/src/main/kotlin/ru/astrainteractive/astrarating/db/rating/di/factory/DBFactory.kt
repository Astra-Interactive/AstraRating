package ru.astrainteractive.astrarating.db.rating.di.factory

import kotlinx.coroutines.runBlocking
import ru.astrainteractive.astralibs.orm.DBConnection
import ru.astrainteractive.astralibs.orm.DBSyntax
import ru.astrainteractive.astralibs.orm.Database
import ru.astrainteractive.astralibs.orm.DefaultDatabase
import ru.astrainteractive.astrarating.db.rating.entity.UserRatingTable
import ru.astrainteractive.astrarating.db.rating.entity.UserTable
import ru.astrainteractive.astrarating.dto.RatingType
import ru.astrainteractive.astrarating.model.ConnectionModel
import ru.astrainteractive.klibs.kdi.Factory
import java.io.File
import java.sql.Connection

class DBFactory(
    private val dataFolder: File,
    private val connectionModel: ConnectionModel
) : Factory<Database> {

    private fun createSqliteDatabase(): Database = DefaultDatabase(
        DBConnection.SQLite("${dataFolder}${File.separator}data.db"),
        DBSyntax.SQLite
    )

    private fun createMySqlDatabase(connection: ConnectionModel.MySqlConnection): Database {
        val dbconnection = DBConnection.MySQL(
            database = connection.database,
            ip = connection.host,
            port = connection.port,
            username = connection.username,
            password = connection.password,
            "sql_mode=''"
        )
        return DefaultDatabase(dbconnection, DBSyntax.MySQL)
    }

    private fun getConnection(): Database {
        return connectionModel.mysql?.let(::createMySqlDatabase) ?: createSqliteDatabase()
    }

    override fun create(): Database = runBlocking {
        val db = getConnection()
        db.openConnection()
        UserRatingTable.create(db)
        UserTable.create(db)
        checkNotNull(db.connection?.let { addCustomType(it) }) { "Could not alter table! Database is not connected!" }
        checkNotNull(db.connection?.let { updateColumn(it) }) { "Could not alter table! Database is not connected!" }
        db
    }

    /**
     * This is needed to update SQL database - add custom type [RatingType]
     */
    private fun addCustomType(connection: Connection) = kotlin.runCatching {
        val statement = connection.createStatement()
        statement.execute(
            """
                    ALTER TABLE ${UserRatingTable.tableName}
                    ADD ${UserRatingTable.ratingTypeIndex.name} ${UserRatingTable.ratingTypeIndex.type} NOT NULL DEFAULT(0)
            """.trimIndent()
        )
        statement.close()
    }

    private fun updateColumn(connection: Connection) = kotlin.runCatching {
        val statement = connection.createStatement()
        statement.execute(
            """                
                ALTER TABLE
                    users_ratings
                RENAME COLUMN user_created_report TO user_created_report_old;            
            """.trimIndent()
        )
        statement.execute(
            """                            
                ALTER TABLE
                    users_ratings
                ADD COLUMN
                    user_created_report INT NULL;
            """.trimIndent()
        )
        statement.execute(
            """                            
                UPDATE users_ratings SET user_created_report=user_created_report_old;
            """.trimIndent()
        )
        statement.execute(
            """
                ALTER TABLE
                    users_ratings
                DROP COLUMN
                    user_created_report_old;            
            """.trimIndent()
        )
        statement.close()
    }
}
