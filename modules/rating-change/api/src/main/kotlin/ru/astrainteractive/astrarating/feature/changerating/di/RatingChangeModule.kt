package ru.astrainteractive.astrarating.feature.changerating.di

import ru.astrainteractive.astrarating.api.rating.api.RatingDao
import ru.astrainteractive.astrarating.core.EmpireConfig
import ru.astrainteractive.astrarating.feature.changerating.data.InsertRatingRepositoryImpl
import ru.astrainteractive.astrarating.feature.changerating.data.InsertUserRepositoryImpl
import ru.astrainteractive.astrarating.feature.changerating.data.PlayerOnPlayerCounterRepositoryImpl
import ru.astrainteractive.astrarating.feature.changerating.data.PlayerTotalRatingRepositoryImpl
import ru.astrainteractive.astrarating.feature.changerating.domain.check.CheckValidatorImpl
import ru.astrainteractive.astrarating.feature.changerating.domain.usecase.AddRatingUseCase
import ru.astrainteractive.astrarating.feature.changerating.domain.usecase.AddRatingUseCaseImpl
import ru.astrainteractive.astrarating.feature.changerating.domain.usecase.InsertUserUseCaseImpl
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

class RatingChangeModule(
    dbApi: RatingDao,
    empireConfigKrate: CachedKrate<EmpireConfig>,
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
            configKrate = empireConfigKrate,
        )
    )
}
