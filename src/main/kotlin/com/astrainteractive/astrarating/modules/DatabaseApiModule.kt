package com.astrainteractive.astrarating.modules

import com.astrainteractive.astrarating.domain.api.DatabaseApi
import ru.astrainteractive.astralibs.di.IModule

object DatabaseApiModule : IModule<DatabaseApi>() {
    override fun initializer(): DatabaseApi {
        return DatabaseApi(DBModule.value)
    }
}