package ru.astrainteractive.astrarating.db.rating.di

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.TransactionManager
import ru.astrainteractive.astralibs.async.CoroutineFeature
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.util.FlowExt.mapCached
import ru.astrainteractive.astrarating.core.di.CoreModule
import ru.astrainteractive.astrarating.db.rating.di.factory.RatingDatabaseFactory
import ru.astrainteractive.astrarating.db.rating.model.DBConnection
import java.io.File

interface DBRatingModule {
    val lifecycle: Lifecycle
    val databaseFlow: Flow<Database>

    class Default(
        private val coreModule: CoreModule,
        private val dataFolder: File
    ) : DBRatingModule {

        override val databaseFlow: Flow<Database> = coreModule.config
            .cachedStateFlow
            .mapCached(CoroutineFeature.Unconfined()) { config, database ->
                database?.run(TransactionManager::closeAndUnregister)
                val connection = when (val mysql = config.databaseConnection.mysql) {
                    null -> DBConnection.SQLite(
                        name = "${dataFolder}${File.separator}data.db"
                    )

                    else -> DBConnection.MySql(
                        url = "jdbc:mysql://${mysql.host}:${mysql.port}/${mysql.database}",
                        user = mysql.username,
                        password = mysql.password
                    )
                }
                RatingDatabaseFactory(connection).create()
            }

        override val lifecycle: Lifecycle by lazy {
            Lifecycle.Lambda(
                onEnable = {},
                onDisable = {
                    runBlocking {
                        databaseFlow.first().run(TransactionManager::closeAndUnregister)
                    }
                }
            )
        }
    }
}
