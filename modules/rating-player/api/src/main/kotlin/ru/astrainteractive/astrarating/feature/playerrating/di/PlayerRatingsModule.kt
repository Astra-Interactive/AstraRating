package ru.astrainteractive.astrarating.feature.playerrating.di

import ru.astrainteractive.astrarating.api.rating.di.ApiRatingModule
import ru.astrainteractive.astrarating.feature.playerrating.domain.SortRatingUseCaseImpl
import ru.astrainteractive.astrarating.feature.playerrating.presentation.DefaultPlayerRatingComponent
import ru.astrainteractive.astrarating.feature.playerrating.presentation.PlayerRatingComponent
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import java.util.UUID

class PlayerRatingsModule(
    private val apiRatingModule: ApiRatingModule,
    private val dispatchers: KotlinDispatchers,
) {
    fun createPlayerRatingsComponent(playerName: String, playerUUID: UUID): PlayerRatingComponent {
        return DefaultPlayerRatingComponent(
            playerName = playerName,
            playerUUID = playerUUID,
            dbApi = apiRatingModule.ratingDBApi,
            dispatchers = dispatchers,
            sortRatingUseCase = SortRatingUseCaseImpl()
        )
    }
}
