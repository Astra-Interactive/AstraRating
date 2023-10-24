package ru.astrainteractive.astrarating.feature.changerating.data

import ru.astrainteractive.astrarating.api.rating.api.RatingDBApi
import ru.astrainteractive.astrarating.dto.UserDTO
import ru.astrainteractive.astrarating.model.UserModel

interface InsertUserRepository {
    suspend fun selectUser(name: String): Result<UserDTO>
    suspend fun insertUser(userModel: UserModel): Result<Long>
}

class InsertUserRepositoryImpl(private val dbApi: RatingDBApi) : InsertUserRepository {
    override suspend fun selectUser(name: String): Result<UserDTO> {
        return dbApi.selectUser(name)
    }

    override suspend fun insertUser(userModel: UserModel): Result<Long> {
        return dbApi.insertUser(userModel)
    }
}
