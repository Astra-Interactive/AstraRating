package ru.astrainteractive.astrarating.data.dao.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import org.jetbrains.exposed.sql.Database
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astrarating.data.dao.RatingCachedDao
import ru.astrainteractive.astrarating.data.dao.RatingDao
import ru.astrainteractive.astrarating.data.dao.impl.RatingCachedDaoImpl
import ru.astrainteractive.astrarating.data.dao.impl.RatingDaoImpl

class RatingDaoModule(
    databaseFlow: Flow<Database>,
    coroutineScope: CoroutineScope,
    isDebugProvider: () -> Boolean
) {

    val ratingDao: RatingDao by lazy {
        RatingDaoImpl(
            databaseFlow = databaseFlow,
            isDebugProvider = isDebugProvider
        )
    }

    val ratingCachedDao: RatingCachedDao by lazy {
        RatingCachedDaoImpl(
            databaseApi = ratingDao,
            scope = coroutineScope
        )
    }

    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onDisable = {
            ratingCachedDao.clear()
        }
    )
}
