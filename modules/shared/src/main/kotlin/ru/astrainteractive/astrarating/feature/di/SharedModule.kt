package ru.astrainteractive.astrarating.feature.di

import kotlinx.coroutines.CoroutineScope
import ru.astrainteractive.astrarating.api.rating.di.ApiRatingModule
import ru.astrainteractive.astrarating.feature.allrating.AllRatingsComponent
import ru.astrainteractive.astrarating.feature.allrating.DefaultAllRatingsComponent
import ru.astrainteractive.astrarating.feature.allrating.data.AllRatingsRepository
import ru.astrainteractive.astrarating.feature.allrating.data.AllRatingsRepositoryImpl
import ru.astrainteractive.astrarating.feature.playerrating.DefaultPlayerRatingsComponent
import ru.astrainteractive.astrarating.feature.playerrating.PlayerRatingsComponent
import ru.astrainteractive.astrarating.model.PlayerModel
import ru.astrainteractive.klibs.kdi.Factory
import ru.astrainteractive.klibs.kdi.Single
import ru.astrainteractive.klibs.kdi.getValue
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

interface SharedModule {
    val allRatingsRepository: AllRatingsRepository

    fun playerRatingsComponentFactory(playerModel: PlayerModel): Factory<PlayerRatingsComponent>
    fun allRatingsComponentFactory(): Factory<AllRatingsComponent>

    class Default(
        private val apiRatingModule: ApiRatingModule,
        private val dispatchers: KotlinDispatchers,
        private val coroutineScope: CoroutineScope
    ) : SharedModule {
        override val allRatingsRepository by Single {
            AllRatingsRepositoryImpl(
                dbApi = apiRatingModule.ratingDBApi,
                coroutineScope = coroutineScope,
                dispatchers = dispatchers
            )
        }

        override fun playerRatingsComponentFactory(playerModel: PlayerModel): Factory<PlayerRatingsComponent> {
            return Factory {
                DefaultPlayerRatingsComponent(
                    playerModel = playerModel,
                    dbApi = apiRatingModule.ratingDBApi,
                    dispatchers = dispatchers
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
