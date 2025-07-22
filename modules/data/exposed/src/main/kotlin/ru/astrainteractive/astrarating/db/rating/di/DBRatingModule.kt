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
import ru.astrainteractive.astralibs.exposed.model.connect
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.logging.JUtiltLogger
import ru.astrainteractive.astralibs.logging.Logger
import ru.astrainteractive.astralibs.serialization.StringFormatExt.parseOrWriteIntoDefault
import ru.astrainteractive.astralibs.util.mapCached
import ru.astrainteractive.astrarating.db.rating.entity.UserRatingTable
import ru.astrainteractive.astrarating.db.rating.entity.UserTable
import ru.astrainteractive.astrarating.db.rating.model.DbRatingConfiguration
import ru.astrainteractive.klibs.kstorage.api.impl.DefaultMutableKrate
import ru.astrainteractive.klibs.kstorage.api.value.ValueFactory
import ru.astrainteractive.klibs.kstorage.util.asStateFlowMutableKrate
import java.io.File

class DBRatingModule(
    stringFormat: StringFormat,
    private val dataFolder: File,
    defaultConfig: ValueFactory<DbRatingConfiguration> = ValueFactory { DbRatingConfiguration() }
) : Logger by JUtiltLogger("AstraRating-DBRatingModule") {
    private val coroutineScope = CoroutineFeature.Default(Dispatchers.IO)

    private val dbConfigurationConfig = DefaultMutableKrate(
        factory = defaultConfig,
        loader = {
            stringFormat.parseOrWriteIntoDefault(
                file = dataFolder.resolve("database.yml"),
                default = defaultConfig::create,
                logger = this
            )
        }
    ).asStateFlowMutableKrate()

    val databaseFlow: Flow<Database> = dbConfigurationConfig
        .cachedStateFlow
        .distinctUntilChangedBy { it.databaseConfiguration }
        .mapCached(coroutineScope) { dbConfig, oldDatabase ->
            oldDatabase?.run(TransactionManager::closeAndUnregister)
            val database = dbConfig.databaseConfiguration.connect(dataFolder)
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

    val lifecycle: Lifecycle by lazy {
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
