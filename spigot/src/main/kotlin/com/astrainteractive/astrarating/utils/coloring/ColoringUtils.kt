package com.astrainteractive.astrarating.utils.coloring

import com.astrainteractive.astrarating.utils.EmpireConfig

object ColoringUtils {
    fun getColoringByRating(colorings: Collection<Coloring>, rating: Int): Coloring {
        if (colorings.isEmpty()) throw IllegalStateException("coloring is empty")
        val sorted = colorings.filter {
            when (it) {
                is Coloring.Equals -> it.value == rating
                is Coloring.Less -> rating < it.value
                is Coloring.More -> rating > it.value
            }
        }.sortedBy { it.value }
        return if (rating < 0) sorted.first()
        else sorted.last()
    }

}