package ru.astrainteractive.astrarating.data.exposed.di

import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.StringFormat
import org.jetbrains.exposed.v1.core.Slf4jSqlDebugLogger
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import ru.astrainteractive.astralibs.coroutines.withTimings
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.util.parseOrWriteIntoDefault
import ru.astrainteractive.astrarating.data.exposed.model.DbRatingConfiguration
import ru.astrainteractive.astrarating.data.exposed.table.UserRatingTable
import ru.astrainteractive.astrarating.data.exposed.table.UserTable
import ru.astrainteractive.klibs.kstorage.api.asStateFlowMutableKrate
import ru.astrainteractive.klibs.kstorage.api.impl.DefaultMutableKrate
import ru.astrainteractive.klibs.kstorage.api.value.ValueFactory
import ru.astrainteractive.klibs.mikro.core.coroutines.CoroutineFeature
import ru.astrainteractive.klibs.mikro.core.coroutines.mapCached
import ru.astrainteractive.klibs.mikro.core.logging.JUtiltLogger
import ru.astrainteractive.klibs.mikro.core.logging.Logger
import ru.astrainteractive.klibs.mikro.exposed.util.connect
import java.io.File
import java.sql.Connection

class ExposedModule(
    stringFormat: StringFormat,
    private val dataFolder: File,
    defaultConfig: ValueFactory<DbRatingConfiguration>
) : Logger by JUtiltLogger("AstraRating-ExposedModule") {
    private val coroutineScope = CoroutineFeature.IO.withTimings()

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
        .distinctUntilChangedBy(DbRatingConfiguration::databaseConfiguration)
        .mapCached(coroutineScope) { dbConfig, oldDatabase ->
            oldDatabase?.run(TransactionManager::closeAndUnregister)
            val database = dbConfig.databaseConfiguration.connect()
            TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
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
            onDisable = {
                runBlocking {
                    databaseFlow.first().run(TransactionManager::closeAndUnregister)
                }
                coroutineScope.cancel()
            }
        )
    }
}
