package com.astrainteractive.astrarating.domain.usecases

import com.astrainteractive.astrarating.domain.api.RatingDBApi
import com.astrainteractive.astrarating.models.UserModel
import ru.astrainteractive.astralibs.domain.UseCase
import java.util.*

/**
 * @param _auction auction to remove
 * @param player owner of auction
 * @return boolean - true if succesfully removed
 */
class InsertUserUseCase(
    private val databaseApi: RatingDBApi,
    val discordIDProvider: (UUID) -> String?
) : UseCase<Int?, UserModel> {
    private val discordUsers = mutableMapOf<String, String>()

    override suspend fun run(params: UserModel): Int? {
        val uuid = params.minecraftUUID.toString()
        val discordID = discordUsers[uuid] ?: discordIDProvider(params.minecraftUUID)?.let {
            discordUsers[uuid] = it
            it
        }
        val user = databaseApi.selectUser(params.minecraftName).getOrNull()
        return user?.let {
            databaseApi.updateUser(it.copy(discordID = discordID))
            it.id
        } ?: databaseApi.insertUser(params).getOrNull()
    }
}
