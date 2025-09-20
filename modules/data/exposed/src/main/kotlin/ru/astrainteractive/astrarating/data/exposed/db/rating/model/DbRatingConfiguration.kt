package ru.astrainteractive.astrarating.data.exposed.db.rating.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.astrainteractive.klibs.mikro.exposed.model.DatabaseConfiguration

@Serializable
data class DbRatingConfiguration(
    @SerialName("rating_database")
    val databaseConfiguration: DatabaseConfiguration = DatabaseConfiguration.H2(
        "./plugins/AstraRating/ASTRA_RATING_RATINGS"
    )
)
