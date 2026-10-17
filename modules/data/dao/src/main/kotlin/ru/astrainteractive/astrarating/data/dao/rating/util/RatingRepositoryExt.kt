package ru.astrainteractive.astrarating.data.dao.rating.util

import ru.astrainteractive.astrarating.data.dao.rating.api.RatingRepository
import ru.astrainteractive.astrarating.data.dao.rating.model.NewUser
import ru.astrainteractive.astrarating.data.dao.rating.model.User

suspend fun RatingRepository.upsertUser(newUser: NewUser): User {
    val selectedUser = selectUser(newUser.minecraftUUID).getOrNull()
    if (selectedUser != null) return selectedUser
    return User(
        id = insertUser(newUser)
            .getOrNull()
            ?: error("Could not insert user $newUser"),
        minecraftName = newUser.minecraftName,
        minecraftUUID = newUser.minecraftUUID.toString()
    )
}
