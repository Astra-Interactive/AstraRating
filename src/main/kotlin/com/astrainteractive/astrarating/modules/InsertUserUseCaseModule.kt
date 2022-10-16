package com.astrainteractive.astrarating.modules

import com.astrainteractive.astrarating.domain.api.use_cases.InsertUserUseCase
import ru.astrainteractive.astralibs.di.IModule

object InsertUserUseCaseModule : IModule<InsertUserUseCase>() {
    override fun initializer(): InsertUserUseCase {
        return InsertUserUseCase(DatabaseApiModule.value)
    }
}