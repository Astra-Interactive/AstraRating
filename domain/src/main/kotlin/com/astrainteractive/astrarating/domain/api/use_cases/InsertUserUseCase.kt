package com.astrainteractive.astrarating.domain.api.use_cases

import com.astrainteractive.astrarating.domain.api.IRatingAPI
import com.astrainteractive.astrarating.domain.entities.User
import ru.astrainteractive.astralibs.domain.IUseCase
import java.util.*

/**
 * @param _auction auction to remove
 * @param player owner of auction
 * @return boolean - true if succesfully removed
 */
class InsertUserUseCase(private val databaseApi: IRatingAPI,val discordIDProvider: (UUID) -> String?) : IUseCase<Long?, InsertUserUseCase.Param> {
    private val discordUsers = mutableMapOf<String, String>()

    class Param(
        val uuid: UUID,
        val name: String,
    )

    override suspend fun run(params: Param): Long? {
        val uuid = params.uuid.toString()
        val discordID = discordUsers[uuid] ?: discordIDProvider(params.uuid)?.let {
            discordUsers[uuid] = it
            it
        }
        val user = databaseApi.selectUser(params.name ?: "NULL")
        return user?.let {
            databaseApi.updateUser(it.copy(discordID = discordID))
            it.id
        } ?: databaseApi.insertUser(
            User(
                minecraftName = params.name ?: "UNDEFINED_NAME",
                minecraftUUID = uuid,
                discordID = discordID,
                lastUpdated = System.currentTimeMillis()
            )
        )
    }
}