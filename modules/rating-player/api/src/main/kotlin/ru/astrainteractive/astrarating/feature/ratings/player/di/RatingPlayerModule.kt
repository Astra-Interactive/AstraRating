package ru.astrainteractive.astrarating.feature.ratings.player.di

import ru.astrainteractive.astrarating.api.rating.di.ApiRatingModule
import ru.astrainteractive.astrarating.feature.ratings.player.domain.RatingSortUseCaseImpl
import ru.astrainteractive.astrarating.feature.ratings.player.presentation.DefaultRatingPlayerComponent
import ru.astrainteractive.astrarating.feature.ratings.player.presentation.RatingPlayerComponent
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import java.util.UUID

class RatingPlayerModule(
    private val apiRatingModule: ApiRatingModule,
    private val dispatchers: KotlinDispatchers,
) {
    fun createPlayerRatingsComponent(playerName: String, playerUUID: UUID): RatingPlayerComponent {
        return DefaultRatingPlayerComponent(
            playerName = playerName,
            playerUUID = playerUUID,
            dbApi = apiRatingModule.ratingDao,
            dispatchers = dispatchers,
            ratingSortUseCase = RatingSortUseCaseImpl()
        )
    }
}
