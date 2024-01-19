package ru.astrainteractive.astrarating.db.rating.di

import org.jetbrains.exposed.sql.Database
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astrarating.core.di.CoreModule
import ru.astrainteractive.astrarating.db.rating.di.factory.RatingDatabaseFactory
import ru.astrainteractive.astrarating.db.rating.model.DBConnection
import ru.astrainteractive.klibs.kdi.Single
import ru.astrainteractive.klibs.kdi.getValue
import java.io.File

interface DBRatingModule {
    val lifecycle: Lifecycle
    val database: Database

    class Default(
        private val coreModule: CoreModule,
        private val dataFolder: File
    ) : DBRatingModule {
        private val connection: DBConnection
            get() = when (val mysql = coreModule.config.value.databaseConnection.mysql) {
                null -> DBConnection.SQLite(
                    name = "${dataFolder}${File.separator}data.db"
                )

                else -> DBConnection.MySql(
                    url = "jdbc:mysql://${mysql.host}:${mysql.port}/${mysql.database}",
                    user = mysql.username,
                    password = mysql.password
                )
            }

        override val database: Database by Single {
            RatingDatabaseFactory(connection).create()
        }

        override val lifecycle: Lifecycle by lazy {
            Lifecycle.Lambda(
                onEnable = {
                    database.connector.invoke().connection
                },
                onDisable = {
                    database.connector.invoke().close()
                }
            )
        }
    }
}
