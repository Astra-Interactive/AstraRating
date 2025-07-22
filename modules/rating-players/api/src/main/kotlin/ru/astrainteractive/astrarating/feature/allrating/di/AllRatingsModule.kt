package ru.astrainteractive.astrarating.feature.allrating.di

import kotlinx.coroutines.CoroutineScope
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astrarating.api.rating.di.ApiRatingModule
import ru.astrainteractive.astrarating.feature.allrating.AllRatingsComponent
import ru.astrainteractive.astrarating.feature.allrating.DefaultAllRatingsComponent
import ru.astrainteractive.astrarating.feature.allrating.data.AllRatingsCachedRepositoryImpl
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

class AllRatingsModule(
    private val apiRatingModule: ApiRatingModule,
    private val dispatchers: KotlinDispatchers,
    private val coroutineScope: CoroutineScope,
) {
    private val allRatingsRepository by lazy {
        AllRatingsCachedRepositoryImpl(
            dbApi = apiRatingModule.ratingDBApi,
            coroutineScope = coroutineScope,
            dispatchers = dispatchers
        )
    }

    fun createAllRatingsComponent(): AllRatingsComponent {
        return DefaultAllRatingsComponent(
            repository = allRatingsRepository,
            dispatchers = dispatchers
        )
    }

    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onDisable = { allRatingsRepository.clear() }
    )
}
