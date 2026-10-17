package ru.astrainteractive.astrarating.gui.api.rating.change.di

import ru.astrainteractive.astrarating.core.settings.AstraRatingConfig
import ru.astrainteractive.astrarating.data.dao.rating.api.RatingRepository
import ru.astrainteractive.astrarating.gui.api.rating.change.usecase.AddRatingUseCase
import ru.astrainteractive.klibs.kstorage.api.CachedKrate

class RatingChangeModule(
    ratingRepository: RatingRepository,
    astraRatingConfigKrate: CachedKrate<AstraRatingConfig>,
) {
    val addRatingUseCase = AddRatingUseCase(
        ratingRepository = ratingRepository,
        configKrate = astraRatingConfigKrate
    )
}
