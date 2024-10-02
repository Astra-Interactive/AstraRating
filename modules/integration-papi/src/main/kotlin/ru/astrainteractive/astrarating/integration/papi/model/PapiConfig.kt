package ru.astrainteractive.astrarating.integration.papi.model

import kotlinx.serialization.Serializable
import ru.astrainteractive.astrarating.integration.papi.coloring.Coloring

@Serializable
internal data class PapiConfig(
    val colorings: List<Coloring> = emptyList()
)
