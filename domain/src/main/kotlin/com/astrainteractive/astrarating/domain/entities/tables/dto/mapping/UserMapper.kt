package com.astrainteractive.astrarating.domain.entities.tables.dto.mapping

import com.astrainteractive.astrarating.domain.entities.tables.dto.UserDTO
import com.astrainteractive.astrarating.domain.entities.tables.UserEntity
import ru.astrainteractive.astralibs.domain.mapping.IMapper

object UserMapper : IMapper<UserEntity, UserDTO> {
    override fun fromDTO(it: UserDTO): UserEntity {
        return (null as UserEntity)
    }

    override fun toDTO(it: UserEntity): UserDTO = UserDTO(
        id = it.id,
        minecraftUUID = it.minecraftUUID,
        minecraftName = it.minecraftName,
        discordID = it.discordID,
        lastUpdated = it.lastUpdated
    )
}