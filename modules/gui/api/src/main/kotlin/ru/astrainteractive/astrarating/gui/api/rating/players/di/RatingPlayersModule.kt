package ru.astrainteractive.astrarating.gui.api.rating.players.di

import kotlinx.coroutines.CoroutineScope
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astrarating.data.dao.rating.api.RatingRepository
import ru.astrainteractive.astrarating.gui.api.rating.players.component.DefaultRatingPlayersComponent
import ru.astrainteractive.astrarating.gui.api.rating.players.component.RatingPlayersComponent
import ru.astrainteractive.astrarating.gui.api.rating.players.util.RatingPlayersCachedRepositoryImpl
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
