package ru.astrainteractive.astrarating.data.dao.rating.util

import ru.astrainteractive.astrarating.data.dao.rating.model.NewUser
import ru.astrainteractive.astrarating.data.dao.rating.model.User

fun User.rename(newUser: NewUser): User = copy(minecraftName = newUser.minecraftName)
