package ru.astrainteractive.astrarating.feature.rating.change.di

import ru.astrainteractive.astrarating.core.settings.AstraRatingConfig
import ru.astrainteractive.astrarating.data.dao.RatingDao
import ru.astrainteractive.astrarating.feature.rating.change.data.InsertRatingRepositoryImpl
import ru.astrainteractive.astrarating.feature.rating.change.data.InsertUserRepositoryImpl
import ru.astrainteractive.astrarating.feature.rating.change.data.PlayerOnPlayerCounterRepositoryImpl
import ru.astrainteractive.astrarating.feature.rating.change.data.PlayerTotalRatingRepositoryImpl
import ru.astrainteractive.astrarating.feature.rating.change.domain.check.CheckValidatorImpl
import ru.astrainteractive.astrarating.feature.rating.change.domain.usecase.AddRatingUseCase
import ru.astrainteractive.astrarating.feature.rating.change.domain.usecase.AddRatingUseCaseImpl
import ru.astrainteractive.astrarating.feature.rating.change.domain.usecase.InsertUserUseCaseImpl
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

class RatingChangeModule(
    dbApi: RatingDao,
    astraRatingConfigKrate: CachedKrate<AstraRatingConfig>,
    dispatchers: KotlinDispatchers,
) {
    val addRatingUseCase: AddRatingUseCase = AddRatingUseCaseImpl(
        insertRatingRepository = InsertRatingRepositoryImpl(
            dbApi = dbApi,
            dispatchers = dispatchers
        ),
        insertUserUseCase = InsertUserUseCaseImpl(
            insertUserRepository = InsertUserRepositoryImpl(
                dbApi = dbApi,
                dispatchers = dispatchers
            )
        ),
        checkValidator = CheckValidatorImpl(
            playerTotalRatingRepository = PlayerTotalRatingRepositoryImpl(
                dbApi = dbApi,
                dispatchers = dispatchers
            ),
            playerOnPlayerCounterRepository = PlayerOnPlayerCounterRepositoryImpl(
                dbApi = dbApi,
                dispatchers = dispatchers
            ),
            configKrate = astraRatingConfigKrate,
        )
    )
}
