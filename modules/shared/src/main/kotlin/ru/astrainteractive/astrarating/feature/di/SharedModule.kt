package ru.astrainteractive.astrarating.feature.di

import kotlinx.coroutines.CoroutineScope
import ru.astrainteractive.astrarating.api.rating.di.ApiRatingModule
import ru.astrainteractive.astrarating.core.EmpireConfig
import ru.astrainteractive.astrarating.feature.allrating.AllRatingsComponent
import ru.astrainteractive.astrarating.feature.allrating.DefaultAllRatingsComponent
import ru.astrainteractive.astrarating.feature.allrating.data.AllRatingsRepositoryImpl
import ru.astrainteractive.astrarating.feature.changerating.di.ChangeRatingModule
import ru.astrainteractive.astrarating.feature.playerrating.domain.SortRatingUseCase
import ru.astrainteractive.astrarating.feature.playerrating.domain.SortRatingUseCaseImpl
import ru.astrainteractive.astrarating.feature.playerrating.presentation.DefaultPlayerRatingsComponent
import ru.astrainteractive.astrarating.feature.playerrating.presentation.PlayerRatingsComponent
import ru.astrainteractive.klibs.kstorage.api.Krate
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

interface SharedModule {
    val changeRatingModule: ChangeRatingModule

    fun createPlayerRatingsComponent(playerName: String): PlayerRatingsComponent
    fun createAllRatingsComponent(): AllRatingsComponent

    class Default(
        private val apiRatingModule: ApiRatingModule,
        private val dispatchers: KotlinDispatchers,
        private val coroutineScope: CoroutineScope,
        private val empireConfigKrate: Krate<EmpireConfig>,
    ) : SharedModule {
        override val changeRatingModule: ChangeRatingModule by lazy {
            ChangeRatingModule.Default(
                dbApi = apiRatingModule.ratingDBApi,
                empireConfigKrate = empireConfigKrate,
                dispatchers = dispatchers,
            )
        }
        private val allRatingsRepository by lazy {
            AllRatingsRepositoryImpl(
                dbApi = apiRatingModule.ratingDBApi,
                coroutineScope = coroutineScope,
                dispatchers = dispatchers
            )
        }
        private val sortRatingUseCase: SortRatingUseCase = SortRatingUseCaseImpl()

        override fun createPlayerRatingsComponent(playerName: String): PlayerRatingsComponent {
            return DefaultPlayerRatingsComponent(
                playerName = playerName,
                dbApi = apiRatingModule.ratingDBApi,
                dispatchers = dispatchers,
                sortRatingUseCase = sortRatingUseCase
            )
        }

        override fun createAllRatingsComponent(): AllRatingsComponent {
            return DefaultAllRatingsComponent(
                repository = allRatingsRepository,
                dispatchers = dispatchers
            )
        }
    }
}
