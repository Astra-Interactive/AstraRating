package ru.astrainteractive.astrarating.dto

data class RatedUserDTO(
    val userDTO: UserDTO,
    val rating: Int
)
