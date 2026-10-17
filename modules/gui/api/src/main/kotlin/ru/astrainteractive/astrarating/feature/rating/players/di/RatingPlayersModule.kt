package ru.astrainteractive.astrarating.feature.rating.players.di

import kotlinx.coroutines.CoroutineScope
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astrarating.data.dao.rating.api.RatingRepository
import ru.astrainteractive.astrarating.feature.rating.players.data.RatingPlayersCachedRepositoryImpl
import ru.astrainteractive.astrarating.feature.rating.players.presentation.DefaultRatingPlayersComponent
import ru.astrainteractive.astrarating.feature.rating.players.presentation.RatingPlayersComponent
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

class RatingPlayersModule(
    private val ratingRepository: RatingRepository,
    private val dispatchers: KotlinDispatchers,
    private val coroutineScope: CoroutineScope,
) {
    private val allRatingsRepository by lazy {
        RatingPlayersCachedRepositoryImpl(
            dbApi = ratingRepository,
            coroutineScope = coroutineScope,
            dispatchers = dispatchers
        )
    }

    fun createAllRatingsComponent(): RatingPlayersComponent {
        return DefaultRatingPlayersComponent(
            repository = allRatingsRepository,
            dispatchers = dispatchers
        )
    }

    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onDisable = { allRatingsRepository.clear() }
    )
}
