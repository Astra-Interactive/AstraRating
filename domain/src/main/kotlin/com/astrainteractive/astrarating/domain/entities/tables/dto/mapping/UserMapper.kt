package com.astrainteractive.astrarating.domain.entities.tables.dto.mapping

import com.astrainteractive.astrarating.domain.entities.User
import com.astrainteractive.astrarating.domain.entities.UserRating
import com.astrainteractive.astrarating.domain.entities.tables.UserEntity
import com.astrainteractive.astrarating.domain.entities.tables.UserRatingEntity
import ru.astrainteractive.astralibs.domain.mapping.IMapper

object UserMapper : IMapper<UserEntity, User> {
    override fun fromDTO(it: User): UserEntity {
        return (null as UserEntity)
    }

    override fun toDTO(it: UserEntity): User = User(
        id = it.id,
        minecraftUUID = it.minecraftUUID,
        minecraftName = it.minecraftName,
        discordID = it.discordID,
        lastUpdated = it.lastUpdated
    )
}