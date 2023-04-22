package com.astrainteractive.astrarating.integrations.coloring

sealed interface Coloring {
    val value: Int
    val color: String
    data class Less(override val value: Int, override val color: String = "#FFFFFF") : Coloring
    data class Equals(override val value: Int, override val color: String = "#FFFFFF") : Coloring
    data class More(override val value: Int, override val color: String = "#FFFFFF") : Coloring
}