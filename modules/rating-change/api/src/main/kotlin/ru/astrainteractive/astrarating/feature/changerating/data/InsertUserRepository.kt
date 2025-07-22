package ru.astrainteractive.astrarating.feature.changerating.data

import kotlinx.coroutines.withContext
import ru.astrainteractive.astrarating.api.rating.api.RatingDao
import ru.astrainteractive.astrarating.dto.UserDTO
import ru.astrainteractive.astrarating.model.UserModel
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import java.util.UUID

internal interface InsertUserRepository {
    suspend fun selectUser(uuid: UUID): Result<UserDTO>
    suspend fun insertUser(userModel: UserModel): Result<Long>
}

internal class InsertUserRepositoryImpl(
    private val dbApi: RatingDao,
    private val dispatchers: KotlinDispatchers
) : InsertUserRepository {
    override suspend fun selectUser(uuid: UUID): Result<UserDTO> {
        return withContext(dispatchers.IO) {
            dbApi.selectUser(uuid)
        }
    }

    override suspend fun insertUser(userModel: UserModel): Result<Long> {
        return withContext(dispatchers.IO) {
            dbApi.insertUser(userModel)
        }
    }
}
