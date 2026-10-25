package ru.astrainteractive.astrarating.integration.papi.placeholder.util

import ru.astrainteractive.astrarating.integration.papi.placeholder.model.Coloring

internal fun Collection<Coloring>.findByRating(rating: Int): Coloring? {
    val matching = filter { coloring -> coloring.matches(rating) }.sortedBy(Coloring::value)
    return if (rating < 0) matching.firstOrNull() else matching.lastOrNull()
}
