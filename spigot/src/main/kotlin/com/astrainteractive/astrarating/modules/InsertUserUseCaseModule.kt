package com.astrainteractive.astrarating.modules

import com.astrainteractive.astrarating.domain.use_cases.InsertUserUseCase
import com.astrainteractive.astrarating.utils.getLinkedDiscordID
import ru.astrainteractive.astralibs.di.Module

object InsertUserUseCaseModule : Module<InsertUserUseCase>() {
    override fun initializer(): InsertUserUseCase {
        return InsertUserUseCase(DatabaseApiModule.value) {
            getLinkedDiscordID(it)
        }
    }
}