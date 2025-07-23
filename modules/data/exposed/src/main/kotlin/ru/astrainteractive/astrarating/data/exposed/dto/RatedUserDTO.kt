package ru.astrainteractive.astrarating.data.exposed.dto

data class RatedUserDTO(
    val userDTO: UserDTO,
    val ratingTotal: Int,
    val ratingCounts: Long
)
