package ru.astrainteractive.astrarating.gui.api.rating.player.di

import ru.astrainteractive.astrarating.data.dao.rating.api.RatingRepository
import ru.astrainteractive.astrarating.gui.api.rating.player.component.DefaultRatingPlayerComponent
import ru.astrainteractive.astrarating.gui.api.rating.player.component.RatingPlayerComponent
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import java.util.UUID

class RatingPlayerModule(
    private val ratingRepository: RatingRepository,
    private val dispatchers: KotlinDispatchers,
) {
    fun createPlayerRatingsComponent(playerName: String, playerUUID: UUID): RatingPlayerComponent {
        return DefaultRatingPlayerComponent(
            playerName = playerName,
            playerUUID = playerUUID,
            ratingRepository = ratingRepository,
            dispatchers = dispatchers
        )
    }
}
