package ru.astrainteractive.astrarating.feature.rating.change.domain.usecase

import ru.astrainteractive.astrarating.data.dao.rating.model.NewUser
import ru.astrainteractive.astrarating.data.dao.rating.model.User
import ru.astrainteractive.astrarating.feature.rating.change.data.InsertUserRepository
import ru.astrainteractive.astrarating.feature.rating.change.domain.usecase.InsertUserUseCase.Input
import ru.astrainteractive.astrarating.feature.rating.change.domain.usecase.InsertUserUseCase.Output
import ru.astrainteractive.astrarating.feature.rating.change.model.RatingPlayer

interface InsertUserUseCase {
    @JvmInline
    value class Input(val playerModel: RatingPlayer)

    @JvmInline
    value class Output(val playerDTO: User)

    suspend operator fun invoke(input: Input): Output
    suspend operator fun invoke(playerModel: RatingPlayer) = invoke(Input(playerModel))
}

internal class InsertUserUseCaseImpl(
    private val insertUserRepository: InsertUserRepository,
) : InsertUserUseCase {
    override suspend fun invoke(input: Input): Output {
        val existedUserOutput = insertUserRepository.selectUser(input.playerModel.uuid)
            .map(User::id)
            .map { id ->
                User(
                    id = id,
                    minecraftName = input.playerModel.name,
                    minecraftUUID = input.playerModel.uuid.toString()
                )
            }
            .map(::Output)
            .getOrNull()
        if (existedUserOutput != null) return existedUserOutput
        val newUser = NewUser(
            minecraftUUID = input.playerModel.uuid,
            minecraftName = input.playerModel.name
        )
        return insertUserRepository.insertUser(newUser)
            .map { id ->
                User(
                    id = id,
                    minecraftName = input.playerModel.name,
                    minecraftUUID = input.playerModel.uuid.toString()
                )
            }
            .map(::Output)
            .getOrThrow()
    }
}
