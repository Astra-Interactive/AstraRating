package ru.astrainteractive.astrarating.db.rating.di

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Slf4jSqlDebugLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import ru.astrainteractive.astralibs.async.CoroutineFeature
import ru.astrainteractive.astralibs.exposed.factory.DatabaseFactory
import ru.astrainteractive.astralibs.exposed.model.DatabaseConfiguration
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.util.FlowExt.mapCached
import ru.astrainteractive.astrarating.db.rating.entity.UserRatingTable
import ru.astrainteractive.astrarating.db.rating.entity.UserTable
import java.io.File

interface DBRatingModule {
    val lifecycle: Lifecycle
    val databaseFlow: Flow<Database>

    class Default(
        private val dbConfigurationFlow: Flow<DatabaseConfiguration>,
        private val dataFolder: File
    ) : DBRatingModule {
        private val coroutineScope = CoroutineFeature.Default(Dispatchers.IO)

        override val databaseFlow: Flow<Database> = dbConfigurationFlow
            .distinctUntilChanged()
            .mapCached(coroutineScope) { dbConfig, oldDatabase ->
                println("Got cached value!")
                oldDatabase?.run(TransactionManager::closeAndUnregister)
                println("Creating database")
                val database = DatabaseFactory(dataFolder).create(dbConfig)
                println("Set transaction")
                TransactionManager.manager.defaultIsolationLevel = java.sql.Connection.TRANSACTION_SERIALIZABLE
                println("Create schema")
                transaction(database) {
                    addLogger(Slf4jSqlDebugLogger)
                    SchemaUtils.create(
                        UserTable,
                        UserRatingTable
                    )
                }
                println("Return database")
                database
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
