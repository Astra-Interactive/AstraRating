package ru.astrainteractive.astrarating.data.exposed.di

import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
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
import ru.astrainteractive.klibs.mikro.core.logging.JUtiltLogger
import ru.astrainteractive.klibs.mikro.core.logging.Logger
import ru.astrainteractive.klibs.mikro.exposed.util.connectAsFlow
import java.io.File
import java.sql.Connection

class ExposedModule(
    stringFormat: StringFormat,
    dataFolder: File,
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

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val databaseFlow: Flow<Database> = dbConfigurationConfig
        .cachedStateFlow
        .distinctUntilChangedBy(DbRatingConfiguration::databaseConfiguration)
        .flatMapLatest { dbConfig -> dbConfig.databaseConfiguration.connectAsFlow() }
        .onEach { database ->
            TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
            transaction(database) {
                addLogger(Slf4jSqlDebugLogger)
                SchemaUtils.create(
                    UserTable,
                    UserRatingTable
                )
            }
        }
        .shareIn(
            scope = coroutineScope,
            started = SharingStarted.Eagerly,
            replay = 1
        )

    @Suppress("TrivialLambdaReference")
    val lifecycle: Lifecycle by lazy {
        Lifecycle.Lambda(
            onDisable = {
                coroutineScope.cancel()
            }
        )
    }
}
