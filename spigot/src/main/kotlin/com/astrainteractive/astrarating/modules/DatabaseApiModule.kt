package com.astrainteractive.astrarating.modules

import com.astrainteractive.astrarating.domain.api.RatingAPI
import com.astrainteractive.astrarating.domain.api.IRatingAPI
import ru.astrainteractive.astralibs.di.IModule

object DatabaseApiModule : IModule<IRatingAPI>() {
    override fun initializer(): IRatingAPI {
        return RatingAPI(DBModule.value)
    }
}