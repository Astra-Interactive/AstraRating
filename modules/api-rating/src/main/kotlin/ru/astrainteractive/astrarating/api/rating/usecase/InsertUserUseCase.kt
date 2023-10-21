package ru.astrainteractive.astrarating.api.rating.usecase

import ru.astrainteractive.astrarating.api.rating.api.RatingDBApi
import ru.astrainteractive.astrarating.model.UserModel
import ru.astrainteractive.klibs.mikro.core.domain.UseCase
import java.util.UUID

/**
 * @param _auction auction to remove
 * @param player owner of auction
 * @return boolean - true if succesfully removed
 */
class InsertUserUseCase(
    private val databaseApi: RatingDBApi,
    val discordIDProvider: (UUID) -> String?
) : UseCase.Parametrized<UserModel, Long?> {
    private val discordUsers = mutableMapOf<String, String>()

    override suspend operator fun invoke(input: UserModel): Long? {
        val uuid = input.minecraftUUID.toString()
        val discordID = discordUsers[uuid] ?: discordIDProvider(input.minecraftUUID)?.let {
            discordUsers[uuid] = it
            it
        }
        val user = databaseApi.selectUser(input.minecraftName).getOrNull()
        return user?.let {
            databaseApi.updateUser(it.copy(discordID = discordID))
            it.id
        } ?: databaseApi.insertUser(input).getOrNull()
    }
}
