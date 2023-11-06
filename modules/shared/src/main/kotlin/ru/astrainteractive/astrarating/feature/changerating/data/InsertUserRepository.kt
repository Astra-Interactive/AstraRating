package ru.astrainteractive.astrarating.feature.changerating.data

import kotlinx.coroutines.withContext
import ru.astrainteractive.astrarating.api.rating.api.RatingDBApi
import ru.astrainteractive.astrarating.dto.UserDTO
import ru.astrainteractive.astrarating.model.UserModel
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

interface InsertUserRepository {
    suspend fun selectUser(name: String): Result<UserDTO>
    suspend fun insertUser(userModel: UserModel): Result<Long>
}

internal class InsertUserRepositoryImpl(
    private val dbApi: RatingDBApi,
    private val dispatchers: KotlinDispatchers
) : InsertUserRepository {
    override suspend fun selectUser(name: String): Result<UserDTO> {
        return withContext(dispatchers.IO) {
            dbApi.selectUser(name)
        }
    }

    override suspend fun insertUser(userModel: UserModel): Result<Long> {
        return withContext(dispatchers.IO) {
            dbApi.insertUser(userModel)
        }
    }
}
