package ru.astrainteractive.astrarating.db.rating.di

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.StringFormat
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Slf4jSqlDebugLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import ru.astrainteractive.astralibs.async.CoroutineFeature
import ru.astrainteractive.astralibs.exposed.factory.DatabaseFactory
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.util.FlowExt.mapCached
import ru.astrainteractive.astrarating.core.di.factory.ConfigKrateFactory
import ru.astrainteractive.astrarating.db.rating.entity.UserRatingTable
import ru.astrainteractive.astrarating.db.rating.entity.UserTable
import ru.astrainteractive.astrarating.db.rating.model.DbRatingConfiguration
import ru.astrainteractive.klibs.kstorage.api.value.ValueFactory
import java.io.File

interface DBRatingModule {
    val lifecycle: Lifecycle
    val databaseFlow: Flow<Database>

    class Default(
        stringFormat: StringFormat,
        private val dataFolder: File,
        defaultConfig: ValueFactory<DbRatingConfiguration> = ValueFactory { DbRatingConfiguration() }
    ) : DBRatingModule {
        private val coroutineScope = CoroutineFeature.Default(Dispatchers.IO)

        private val dbConfigurationConfig = ConfigKrateFactory.create<DbRatingConfiguration>(
            fileNameWithoutExtension = "database",
            dataFolder = dataFolder,
            stringFormat = stringFormat,
            factory = defaultConfig
        )

        override val databaseFlow: Flow<Database> = dbConfigurationConfig
            .cachedStateFlow
            .distinctUntilChangedBy { it.databaseConfiguration }
            .mapCached(coroutineScope) { dbConfig, oldDatabase ->
                oldDatabase?.run(TransactionManager::closeAndUnregister)
                val database = DatabaseFactory(dataFolder).create(dbConfig.databaseConfiguration)
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
                    coroutineScope.cancel()
                }
            )
        }
    }
}
