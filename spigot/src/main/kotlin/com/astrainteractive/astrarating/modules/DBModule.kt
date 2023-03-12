package com.astrainteractive.astrarating.modules

import com.astrainteractive.astrarating.AstraRating
import com.astrainteractive.astrarating.domain.entities.tables.UserRatingTable
import com.astrainteractive.astrarating.domain.entities.tables.UserTable
import com.astrainteractive.astrarating.domain.entities.tables.dto.RatingTypeDTO
import com.astrainteractive.astrarating.utils.EmpireConfig
import kotlinx.coroutines.runBlocking
import ru.astrainteractive.astralibs.di.IModule
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astralibs.orm.DBConnection
import ru.astrainteractive.astralibs.orm.DBSyntax
import ru.astrainteractive.astralibs.orm.Database
import ru.astrainteractive.astralibs.orm.DefaultDatabase
import java.io.File
import java.sql.Connection

object DBModule : IModule<Database>() {
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
        val config by ConfigProvider
        val db = getConnection(config)
        db.openConnection()
        UserRatingTable.create(db)
        UserTable.create(db)
        db.connection?.let(::addCustomType)
        db
    }

    /**
     * This is needed to update SQL database - add custom type [RatingTypeDTO]
     */
    private fun addCustomType(connection: Connection) {
        val statement = connection.createStatement()
        statement.execute(
            """
                    ALTER TABLE ${UserRatingTable.tableName}
                    ADD ${UserRatingTable.ratingTypeIndex.name} ${UserRatingTable.ratingTypeIndex.type} NOT NULL DEFAULT(0)
                """.trimIndent()
        )
        statement.close()
    }
}