package com.astrainteractive.astrarating.domain.api.use_cases

import com.astrainteractive.astrarating.domain.api.RatingAPI
import ru.astrainteractive.astralibs.domain.IUseCase

class VoteUseCase(
    private val databaseApi: RatingAPI
) : IUseCase<Unit, VoteUseCase.Param> {
    class Param()

    override suspend fun run(params: Param) {
        TODO("Not yet implemented")
    }
}