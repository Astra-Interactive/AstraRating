package ru.astrainteractive.astrarating.integration.papi.coloring

internal object ColoringUtil {
    /**
     * @return null when no coloring covers [rating], as with the default empty `papi.yml`
     */
    fun getColoringByRating(colorings: Collection<Coloring>, rating: Int): Coloring? {
        val sorted = colorings.filter {
            when (it) {
                is Coloring.Equals -> it.value == rating
                is Coloring.Less -> rating < it.value
                is Coloring.More -> rating > it.value
            }
        }.sortedBy { it.value }
        return if (rating < 0) {
            sorted.firstOrNull()
        } else {
            sorted.lastOrNull()
        }
    }
}
