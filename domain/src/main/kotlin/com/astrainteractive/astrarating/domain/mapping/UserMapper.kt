package com.astrainteractive.astrarating.domain.mapping

import com.astrainteractive.astrarating.domain.entities.UserEntity
import com.astrainteractive.astrarating.dto.UserDTO
import ru.astrainteractive.astralibs.domain.mapping.Mapper

object UserMapper : Mapper<UserEntity, UserDTO> {
    override fun fromDTO(it: UserDTO): UserEntity {
        error("Method not implemented!")
    }

    override fun toDTO(it: UserEntity): UserDTO = UserDTO(
        id = it.id,
        minecraftUUID = it.minecraftUUID,
        minecraftName = it.minecraftName,
        discordID = it.discordID,
        lastUpdated = it.lastUpdated
    )
}
