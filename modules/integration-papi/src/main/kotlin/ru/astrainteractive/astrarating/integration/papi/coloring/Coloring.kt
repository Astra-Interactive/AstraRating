package ru.astrainteractive.astrarating.integration.papi.coloring

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal sealed interface Coloring {
    val value: Int
    val color: String

    @SerialName("EQUALS")
    @Serializable
    data class Equals(
        override val value: Int,
        override val color: String = "#FFFFFF"
    ) : Coloring

    @SerialName("LESS")
    @Serializable
    data class Less(
        override val value: Int,
        override val color: String = "#FFFFFF"
    ) : Coloring

    @SerialName("MORE")
    @Serializable
    data class More(
        override val value: Int,
        override val color: String = "#FFFFFF"
    ) : Coloring
}
