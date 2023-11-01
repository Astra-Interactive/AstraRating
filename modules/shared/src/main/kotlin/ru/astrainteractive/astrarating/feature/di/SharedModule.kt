package ru.astrainteractive.astrarating.feature.di

import kotlinx.coroutines.CoroutineScope
import ru.astrainteractive.astralibs.permission.PermissionManager
import ru.astrainteractive.astrarating.api.rating.di.ApiRatingModule
import ru.astrainteractive.astrarating.feature.allrating.AllRatingsComponent
import ru.astrainteractive.astrarating.feature.allrating.DefaultAllRatingsComponent
import ru.astrainteractive.astrarating.feature.allrating.data.AllRatingsRepository
import ru.astrainteractive.astrarating.feature.allrating.data.AllRatingsRepositoryImpl
import ru.astrainteractive.astrarating.feature.changerating.di.ChangeRatingModule
import ru.astrainteractive.astrarating.feature.playerrating.domain.SortRatingUseCase
import ru.astrainteractive.astrarating.feature.playerrating.domain.SortRatingUseCaseImpl
import ru.astrainteractive.astrarating.feature.playerrating.presentation.DefaultPlayerRatingsComponent
import ru.astrainteractive.astrarating.feature.playerrating.presentation.PlayerRatingsComponent
import ru.astrainteractive.astrarating.model.EmpireConfig
import ru.astrainteractive.astrarating.model.PlayerModel
import ru.astrainteractive.klibs.kdi.Factory
import ru.astrainteractive.klibs.kdi.Reloadable
import ru.astrainteractive.klibs.kdi.Single
import ru.astrainteractive.klibs.kdi.getValue
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

interface SharedModule {
    val changeRatingModule: ChangeRatingModule

    val allRatingsRepository: AllRatingsRepository

    fun playerRatingsComponentFactory(playerModel: PlayerModel): Factory<PlayerRatingsComponent>
    fun allRatingsComponentFactory(): Factory<AllRatingsComponent>

    class Default(
        private val apiRatingModule: ApiRatingModule,
        private val dispatchers: KotlinDispatchers,
        private val coroutineScope: CoroutineScope,
        private val permissionManager: PermissionManager,
        private val empireConfig: Reloadable<EmpireConfig>
    ) : SharedModule {
        override val changeRatingModule: ChangeRatingModule by Single {
            ChangeRatingModule.Default(
                dbApi = apiRatingModule.ratingDBApi,
                permissionManager = permissionManager,
                empireConfig = empireConfig
            )
        }
        override val allRatingsRepository by Single {
            AllRatingsRepositoryImpl(
                dbApi = apiRatingModule.ratingDBApi,
                coroutineScope = coroutineScope,
                dispatchers = dispatchers
            )
        }
        private val sortRatingUseCase: SortRatingUseCase = SortRatingUseCaseImpl()

        override fun playerRatingsComponentFactory(playerModel: PlayerModel): Factory<PlayerRatingsComponent> {
            return Factory {
                DefaultPlayerRatingsComponent(
                    playerModel = playerModel,
                    dbApi = apiRatingModule.ratingDBApi,
                    dispatchers = dispatchers,
                    sortRatingUseCase = sortRatingUseCase
                )
            }
        }

        override fun allRatingsComponentFactory(): Factory<AllRatingsComponent> {
            return Factory {
                DefaultAllRatingsComponent(
                    repository = allRatingsRepository,
                    dispatchers = dispatchers
                )
            }
        }
    }
}
