package ru.astrainteractive.astrarating.gui.api.rating.players.di

import kotlinx.coroutines.CoroutineScope
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astrarating.data.dao.rating.api.RatingRepository
import ru.astrainteractive.astrarating.gui.api.rating.players.component.DefaultRatingPlayersComponent
import ru.astrainteractive.astrarating.gui.api.rating.players.component.RatingPlayersComponent
import ru.astrainteractive.astrarating.gui.api.rating.players.util.RatedUsersCache
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

class RatingPlayersModule(
    private val ratingRepository: RatingRepository,
    private val dispatchers: KotlinDispatchers,
    private val coroutineScope: CoroutineScope,
) {
    private val ratedUsersCache by lazy {
        RatedUsersCache(
            ratingRepository = ratingRepository,
            dispatchers = dispatchers,
            coroutineScope = coroutineScope
        )
    }

    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onDisable = { ratedUsersCache.clear() }
    )

    fun createAllRatingsComponent(): RatingPlayersComponent {
        return DefaultRatingPlayersComponent(
            ratedUsersCache = ratedUsersCache,
            dispatchers = dispatchers
        )
    }
}
