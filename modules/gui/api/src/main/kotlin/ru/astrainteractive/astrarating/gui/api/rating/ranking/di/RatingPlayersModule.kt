package ru.astrainteractive.astrarating.gui.api.rating.ranking.di

import ru.astrainteractive.astrarating.data.dao.rating.api.RatedUsersCache
import ru.astrainteractive.astrarating.gui.api.rating.ranking.component.DefaultRatingPlayersComponent
import ru.astrainteractive.astrarating.gui.api.rating.ranking.component.RatingPlayersComponent
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

class RatingPlayersModule(
    private val ratedUsersCache: RatedUsersCache,
    private val dispatchers: KotlinDispatchers
) {
    fun createAllRatingsComponent(): RatingPlayersComponent {
        return DefaultRatingPlayersComponent(
            ratedUsersCache = ratedUsersCache,
            dispatchers = dispatchers
        )
    }
}
