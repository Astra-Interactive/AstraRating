package ru.astrainteractive.astrarating.feature.di

import kotlinx.coroutines.CoroutineScope
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astrarating.api.rating.di.ApiRatingModule
import ru.astrainteractive.astrarating.core.EmpireConfig
import ru.astrainteractive.astrarating.feature.allrating.AllRatingsFeature
import ru.astrainteractive.astrarating.feature.allrating.data.AllRatingsCachedRepositoryImpl
import ru.astrainteractive.astrarating.feature.changerating.di.ChangeRatingModule
import ru.astrainteractive.astrarating.feature.playerrating.domain.SortRatingUseCaseImpl
import ru.astrainteractive.astrarating.feature.playerrating.presentation.PlayerRatingsFeature
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import java.util.UUID

interface SharedModule {
    val lifecycle: Lifecycle

    val changeRatingModule: ChangeRatingModule

    fun createPlayerRatingsComponent(
        playerName: String,
        playerUUID: UUID
    ): PlayerRatingsFeature

    fun createAllRatingsComponent(): AllRatingsFeature

    class Default(
        private val apiRatingModule: ApiRatingModule,
        private val dispatchers: KotlinDispatchers,
        private val coroutineScope: CoroutineScope,
        private val empireConfigKrate: CachedKrate<EmpireConfig>,
    ) : SharedModule {
        override val changeRatingModule: ChangeRatingModule by lazy {
            ChangeRatingModule.Default(
                dbApi = apiRatingModule.ratingDBApi,
                empireConfigKrate = empireConfigKrate,
                dispatchers = dispatchers,
            )
        }
        private val allRatingsRepository by lazy {
            AllRatingsCachedRepositoryImpl(
                dbApi = apiRatingModule.ratingDBApi,
                coroutineScope = coroutineScope,
                dispatchers = dispatchers
            )
        }

        override fun createPlayerRatingsComponent(playerName: String, playerUUID: UUID): PlayerRatingsFeature {
            return PlayerRatingsFeature(
                playerName = playerName,
                playerUUID = playerUUID,
                dbApi = apiRatingModule.ratingDBApi,
                dispatchers = dispatchers,
                sortRatingUseCase = SortRatingUseCaseImpl()
            )
        }

        override fun createAllRatingsComponent(): AllRatingsFeature {
            return AllRatingsFeature(
                repository = allRatingsRepository,
                dispatchers = dispatchers
            )
        }

        override val lifecycle: Lifecycle = Lifecycle.Lambda(
            onDisable = { allRatingsRepository.clear() }
        )
    }
}
