package ru.astrainteractive.astrarating.data.dao

import ru.astrainteractive.astrarating.data.exposed.dto.UserDTO
import ru.astrainteractive.astrarating.data.exposed.model.UserModel

suspend fun RatingDao.upsertUser(userModel: UserModel): UserDTO {
    val selectedUser = selectUser(userModel.minecraftUUID).getOrNull()
    if (selectedUser != null) return selectedUser
    return UserDTO(
        id = insertUser(userModel)
            .getOrNull()
            ?: error("Could not insert user $userModel"),
        minecraftName = userModel.minecraftName,
        minecraftUUID = userModel.minecraftUUID.toString()
    )
}
