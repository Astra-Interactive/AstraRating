package ru.astrainteractive.astrarating.api.rating.usecase

import ru.astrainteractive.astrarating.api.rating.api.RatingDBApi
import ru.astrainteractive.astrarating.model.UserModel
import ru.astrainteractive.klibs.mikro.core.domain.UseCase

/**
 * @param _auction auction to remove
 * @param player owner of auction
 * @return boolean - true if succesfully removed
 */
interface InsertUserUseCase : UseCase.Parametrized<UserModel, Long?>

internal class InsertUserUseCaseImpl(
    private val databaseApi: RatingDBApi,
) : InsertUserUseCase {
    override suspend operator fun invoke(input: UserModel): Long? {
        val existedUserId = databaseApi.selectUser(input.minecraftName).getOrNull()?.id
        if (existedUserId != null) return existedUserId
        return databaseApi.insertUser(input).getOrNull()
    }
}
