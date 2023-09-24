package ru.astrainteractive.astrarating.db.rating.mapping

import ru.astrainteractive.astrarating.db.rating.entity.UserEntity
import ru.astrainteractive.astrarating.dto.UserDTO
import ru.astrainteractive.klibs.mikro.core.domain.Mapper

object UserMapper : Mapper<UserEntity, UserDTO> {
    override fun fromDTO(it: UserDTO): UserEntity {
        error("Method not implemented!")
    }

    override fun toDTO(it: UserEntity): UserDTO = UserDTO(
        id = it.id,
        minecraftUUID = it.minecraftUUID,
        minecraftName = it.minecraftName,
        discordID = null,
        lastUpdated = it.lastUpdated
    )
}
