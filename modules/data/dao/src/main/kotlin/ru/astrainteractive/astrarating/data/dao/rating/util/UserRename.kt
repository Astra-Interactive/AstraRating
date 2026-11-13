package ru.astrainteractive.astrarating.data.dao.rating.util

import ru.astrainteractive.astrarating.data.dao.rating.model.NewUser
import ru.astrainteractive.astrarating.data.dao.rating.model.User

fun isUserRenamed(storedUser: User, newUser: NewUser): Boolean {
    return storedUser.minecraftName != newUser.minecraftName
}
