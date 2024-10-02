package ru.astrainteractive.astrarating.db.rating.di

import kotlinx.coroutines.flow.Flow
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

        override val databaseFlow: Flow<Database> = dbConfigurationFlow
            .mapCached(CoroutineFeature.Unconfined()) { dbConfig, oldDatabase ->
                oldDatabase?.run(TransactionManager::closeAndUnregister)
                val database = DatabaseFactory(dataFolder).create(dbConfig)
                TransactionManager.manager.defaultIsolationLevel = java.sql.Connection.TRANSACTION_SERIALIZABLE
                transaction(database) {
                    addLogger(Slf4jSqlDebugLogger)
                    SchemaUtils.create(
                        UserTable,
                        UserRatingTable
                    )
                }
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
