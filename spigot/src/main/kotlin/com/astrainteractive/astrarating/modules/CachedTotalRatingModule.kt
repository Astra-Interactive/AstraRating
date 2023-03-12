package com.astrainteractive.astrarating.modules

import com.astrainteractive.astrarating.domain.api.CachedTotalRating
import ru.astrainteractive.astralibs.di.Module

object CachedTotalRatingModule : Module<CachedTotalRating>() {
    override fun initializer(): CachedTotalRating {
        return CachedTotalRating(DatabaseApiModule.value)
    }
}