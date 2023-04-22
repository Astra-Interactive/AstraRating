package com.astrainteractive.astrarating.modules

import com.astrainteractive.astrarating.AstraRating
import com.astrainteractive.astrarating.domain.entities.UserRatingTable
import com.astrainteractive.astrarating.domain.entities.UserTable
import com.astrainteractive.astrarating.dto.RatingType
import com.astrainteractive.astrarating.plugin.EmpireConfig
import kotlinx.coroutines.runBlocking
import ru.astrainteractive.astralibs.di.Dependency
import ru.astrainteractive.astralibs.di.Factory
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astralibs.orm.DBConnection
import ru.astrainteractive.astralibs.orm.DBSyntax
import ru.astrainteractive.astralibs.orm.Database
import ru.astrainteractive.astralibs.orm.DefaultDatabase
import java.io.File
import java.sql.Connection

class DBFactory(
    val config: Dependency<EmpireConfig>
) : Factory<Database>() {
    private fun createSqliteDatabase(): Database = DefaultDatabase(
        DBConnection.SQLite("${AstraRating.instance.dataFolder}${File.separator}data.db"),
        DBSyntax.SQLite
    )

    private fun createMySqlDatabase(config: EmpireConfig.Connection.MySqlConnection): Database {
        val dbconnection = DBConnection.MySQL(
            database = config.database,
            ip = config.host,
            port = config.port,
            username = config.username,
            password = config.password
        )
        return DefaultDatabase(dbconnection, DBSyntax.MySQL)
    }

    private fun getConnection(config: EmpireConfig): Database {
        return config.databaseConnection.mysql?.let(::createMySqlDatabase) ?: createSqliteDatabase()
    }

    override fun initializer(): Database = runBlocking {
        val config by config
        val db = getConnection(config)
        db.openConnection()
        UserRatingTable.create(db)
        UserTable.create(db)
        db.connection?.let { addCustomType(it) } ?: throw Exception("Could not alter table! Database is not connected!")
        db.connection?.let { updateColumn(it) } ?: throw Exception("Could not alter table! Database is not connected!")
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
        val tableName = UserRatingTable.tableName
        val columnName = UserRatingTable.userCreatedReport.name
        val columnNameOld = "${UserRatingTable.userCreatedReport.name}_old"
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