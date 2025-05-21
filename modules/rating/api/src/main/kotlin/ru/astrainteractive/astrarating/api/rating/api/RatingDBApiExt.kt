package ru.astrainteractive.astrarating.api.rating.api

import ru.astrainteractive.astrarating.dto.UserDTO
import ru.astrainteractive.astrarating.model.UserModel

object RatingDBApiExt {
    suspend fun RatingDBApi.upsertUser(userModel: UserModel): UserDTO {
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
}
