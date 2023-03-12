package com.astrainteractive.astrarating.modules

import com.astrainteractive.astrarating.domain.api.RatingDBApi
import com.astrainteractive.astrarating.domain.api.RatingDBApiImpl
import ru.astrainteractive.astralibs.di.Module

object DatabaseApiModule : Module<RatingDBApi>() {
    override fun initializer(): RatingDBApi {
        return RatingDBApiImpl(DBModule.value)
    }
}