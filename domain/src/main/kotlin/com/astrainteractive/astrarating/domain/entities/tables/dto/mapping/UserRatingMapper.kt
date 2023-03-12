package com.astrainteractive.astrarating.domain.entities.tables.dto.mapping

import com.astrainteractive.astrarating.domain.entities.tables.dto.UserRatingDTO
import com.astrainteractive.astrarating.domain.entities.tables.UserRatingEntity
import com.astrainteractive.astrarating.domain.entities.tables.dto.RatingTypeDTO
import ru.astrainteractive.astralibs.domain.mapping.IMapper

object UserRatingMapper : IMapper<UserRatingEntity, UserRatingDTO> {
    override fun fromDTO(it: UserRatingDTO): UserRatingEntity {
        return (null as UserRatingEntity)
    }

    override fun toDTO(it: UserRatingEntity): UserRatingDTO = UserRatingDTO(
        id = it.id,
        userCreatedReport = it.userCreatedReport,
        reportedUser = it.reportedUser,
        rating = it.rating,
        message = it.message,
        ratingType = RatingTypeDTO.values().getOrElse(it.ratingTypeIndex) { RatingTypeDTO.values().first() },
        time = it.time
    )
}