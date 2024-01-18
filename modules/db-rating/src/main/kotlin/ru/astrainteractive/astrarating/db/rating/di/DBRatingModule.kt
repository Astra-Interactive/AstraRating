package ru.astrainteractive.astrarating.db.rating.di

import org.jetbrains.exposed.sql.Database
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astrarating.db.rating.di.factory.RatingDatabaseFactory
import ru.astrainteractive.astrarating.db.rating.model.DBConnection
import ru.astrainteractive.klibs.kdi.Single
import ru.astrainteractive.klibs.kdi.getValue

interface DBRatingModule {
    val lifecycle: Lifecycle
    val database: Database

    class Default(connection: DBConnection) : DBRatingModule {
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
