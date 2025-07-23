package ru.astrainteractive.astrarating.feature.rating.change.domain.usecase

import ru.astrainteractive.astrarating.data.exposed.dto.UserDTO
import ru.astrainteractive.astrarating.data.exposed.model.PlayerModel
import ru.astrainteractive.astrarating.data.exposed.model.UserModel
import ru.astrainteractive.astrarating.feature.rating.change.data.InsertUserRepository
import ru.astrainteractive.astrarating.feature.rating.change.domain.usecase.InsertUserUseCase.Input
import ru.astrainteractive.astrarating.feature.rating.change.domain.usecase.InsertUserUseCase.Output

/**
 * @param _auction auction to remove
 * @param player owner of auction
 * @return boolean - true if succesfully removed
 */
interface InsertUserUseCase {
    @JvmInline
    value class Input(val playerModel: PlayerModel)

    @JvmInline
    value class Output(val playerDTO: UserDTO)

    suspend operator fun invoke(input: Input): Output
    suspend operator fun invoke(playerModel: PlayerModel) = invoke(Input(playerModel))
}

internal class InsertUserUseCaseImpl(
    private val insertUserRepository: InsertUserRepository,
) : InsertUserUseCase {
    override suspend fun invoke(input: Input): Output {
        val existedUserOutput = insertUserRepository.selectUser(input.playerModel.uuid)
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
