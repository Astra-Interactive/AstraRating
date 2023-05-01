package com.astrainteractive.astrarating.integrations.papi.coloring

object ColoringUtils {
    fun getColoringByRating(colorings: Collection<Coloring>, rating: Int): Coloring {
        check(colorings.isNotEmpty()) { "coloring is empty" }
        val sorted = colorings.filter {
            when (it) {
                is Coloring.Equals -> it.value == rating
                is Coloring.Less -> rating < it.value
                is Coloring.More -> rating > it.value
            }
        }.sortedBy { it.value }
        return if (rating < 0) {
            sorted.first()
        } else {
            sorted.last()
        }
    }
}
