package ru.astrainteractive.astrarating.api.rating.di

import kotlinx.coroutines.CoroutineScope
import org.jetbrains.exposed.sql.Database
import ru.astrainteractive.astrarating.api.rating.api.CachedApi
import ru.astrainteractive.astrarating.api.rating.api.RatingDBApi
import ru.astrainteractive.astrarating.api.rating.api.impl.CachedApiImpl
import ru.astrainteractive.astrarating.api.rating.api.impl.RatingDBApiImpl
import ru.astrainteractive.astrarating.api.rating.usecase.InsertUserUseCase
import ru.astrainteractive.astrarating.api.rating.usecase.InsertUserUseCaseImpl
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.getValue
import java.io.File

interface ApiRatingModule {
    val ratingDBApi: RatingDBApi
    val cachedApi: CachedApi
    val insertUserUseCase: InsertUserUseCase

    class Default(
        database: Database,
        coroutineScope: CoroutineScope,
        pluginFolder: File
    ) : ApiRatingModule {

        override val ratingDBApi: RatingDBApi by Provider {
            RatingDBApiImpl(
                database = database,
                pluginFolder = pluginFolder
            )
        }
        override val cachedApi: CachedApi by Provider {
            CachedApiImpl(
                databaseApi = ratingDBApi,
                scope = coroutineScope
            )
        }
        override val insertUserUseCase: InsertUserUseCase by Provider {
            InsertUserUseCaseImpl(
                databaseApi = ratingDBApi
            )
        }
    }
}
