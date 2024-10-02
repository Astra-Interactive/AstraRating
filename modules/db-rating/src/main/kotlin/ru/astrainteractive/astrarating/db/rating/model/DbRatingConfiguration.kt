package ru.astrainteractive.astrarating.db.rating.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.astrainteractive.astralibs.exposed.model.DatabaseConfiguration

@Serializable
data class DbRatingConfiguration(
    @SerialName("rating_database")
    val databaseConfiguration: DatabaseConfiguration = DatabaseConfiguration.H2("ASTRA_RATING_RATINGS")
)
