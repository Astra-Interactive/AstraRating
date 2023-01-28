package com.astrainteractive.astrarating.modules

import com.astrainteractive.astrarating.domain.api.IRatingAPI
import com.astrainteractive.astrarating.domain.api.TableAPI
import kotlinx.coroutines.runBlocking
import ru.astrainteractive.astralibs.di.IModule

object DatabaseApiModule : IModule<IRatingAPI>() {
    override fun initializer(): IRatingAPI {
        return TableAPI(DBModule.value)
    }
}