package ru.astrainteractive.astrarating.feature.changerating.domain.usecase

import ru.astrainteractive.astrarating.dto.UserDTO
import ru.astrainteractive.astrarating.feature.changerating.data.InsertUserRepository
import ru.astrainteractive.astrarating.feature.changerating.domain.usecase.InsertUserUseCase.Input
import ru.astrainteractive.astrarating.feature.changerating.domain.usecase.InsertUserUseCase.Output
import ru.astrainteractive.astrarating.model.PlayerModel
import ru.astrainteractive.astrarating.model.UserModel
import ru.astrainteractive.klibs.mikro.core.domain.UseCase

/**
 * @param _auction auction to remove
 * @param player owner of auction
 * @return boolean - true if succesfully removed
 */
interface InsertUserUseCase : UseCase.Suspended<Input, Output> {
    @JvmInline
    value class Input(val playerModel: PlayerModel)

    @JvmInline
    value class Output(val playerDTO: UserDTO)

    suspend operator fun invoke(playerModel: PlayerModel) = invoke(Input(playerModel))
}

internal class InsertUserUseCaseImpl(
    private val insertUserRepository: InsertUserRepository,
) : InsertUserUseCase {
    override suspend fun invoke(input: Input): Output {
        val existedUserOutput = insertUserRepository.selectUser(input.playerModel.name)
            .map(UserDTO::id)
            .map { id ->
                UserDTO(
                    id = id,
                    minecraftName = input.playerModel.name,
                    minecraftUUID = input.playerModel.uuid.toString()
                )
            }
            .map(::Output)
            .getOrNull()
        if (existedUserOutput != null) return existedUserOutput
        val userModel = UserModel(
            minecraftUUID = input.playerModel.uuid,
            minecraftName = input.playerModel.name
        )
        return insertUserRepository.insertUser(userModel)
            .map { id ->
                UserDTO(
                    id = id,
                    minecraftName = input.playerModel.name,
                    minecraftUUID = input.playerModel.uuid.toString()
                )
            }
            .map(::Output)
            .getOrThrow()
    }
}
