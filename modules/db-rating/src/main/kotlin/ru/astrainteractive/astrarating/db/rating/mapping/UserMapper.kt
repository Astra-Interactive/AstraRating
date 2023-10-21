package ru.astrainteractive.astrarating.db.rating.mapping

import ru.astrainteractive.astrarating.db.rating.entity.UserDAO
import ru.astrainteractive.astrarating.dto.UserDTO
import ru.astrainteractive.klibs.mikro.core.domain.Mapper

object UserMapper : Mapper<UserDAO, UserDTO> {
    override fun fromDTO(it: UserDTO): UserDAO {
        error("Method not implemented!")
    }

    override fun toDTO(it: UserDAO): UserDTO = UserDTO(
        id = it.id.value,
        minecraftUUID = it.minecraftUUID,
        minecraftName = it.minecraftName,
        lastUpdated = it.lastUpdated
    )
}
