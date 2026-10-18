package ru.astrainteractive.astrarating.data.dao.rating.util

import ru.astrainteractive.astrarating.data.dao.rating.api.RatingRepository
import ru.astrainteractive.astrarating.data.dao.rating.model.NewUser
import ru.astrainteractive.astrarating.data.dao.rating.model.User

suspend fun RatingRepository.upsertUser(newUser: NewUser): Result<User> {
    val storedUser = findUser(newUser.minecraftUUID).getOrElse { error -> return Result.failure(error) }
        ?: return insertUser(newUser)
    if (storedUser.minecraftName == newUser.minecraftName) return Result.success(storedUser)
    val renamedUser = storedUser.copy(minecraftName = newUser.minecraftName)
    return updateUser(renamedUser).map { _ -> renamedUser }
}
