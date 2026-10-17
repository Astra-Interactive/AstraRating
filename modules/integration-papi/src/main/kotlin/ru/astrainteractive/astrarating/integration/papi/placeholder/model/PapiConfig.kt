package ru.astrainteractive.astrarating.integration.papi.placeholder.model

import kotlinx.serialization.Serializable

@Serializable
internal data class PapiConfig(
    val colorings: List<Coloring> = emptyList()
)
