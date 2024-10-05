package ru.astrainteractive.astrarating.dto

data class RatedUserDTO(
    val userDTO: UserDTO,
    val ratingTotal: Int,
    val ratingCounts: Long
)
