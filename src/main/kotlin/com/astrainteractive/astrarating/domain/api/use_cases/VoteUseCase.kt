package com.astrainteractive.astrarating.domain.api.use_cases

import com.astrainteractive.astrarating.domain.api.DatabaseApi
import ru.astrainteractive.astralibs.domain.IUseCase

class VoteUseCase(
    private val databaseApi: DatabaseApi
) : IUseCase<Unit, VoteUseCase.Param> {
    class Param()

    override suspend fun run(params: Param) {
        TODO("Not yet implemented")
    }
}