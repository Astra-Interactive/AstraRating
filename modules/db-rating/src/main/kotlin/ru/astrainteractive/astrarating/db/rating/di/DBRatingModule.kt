package ru.astrainteractive.astrarating.db.rating.di

import org.jetbrains.exposed.sql.Database
import ru.astrainteractive.astrarating.db.rating.di.factory.RatingDatabaseFactory
import ru.astrainteractive.astrarating.db.rating.model.DBConnection
import ru.astrainteractive.klibs.kdi.Single
import ru.astrainteractive.klibs.kdi.getValue

interface DBRatingModule {
    val database: Database

    class Default(connection: DBConnection) : DBRatingModule {
        override val database: Database by Single {
            RatingDatabaseFactory(connection).create()
        }
    }
}
