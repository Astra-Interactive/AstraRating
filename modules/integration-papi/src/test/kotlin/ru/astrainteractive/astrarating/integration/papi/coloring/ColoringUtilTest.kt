@file:Suppress("FunctionNaming")

package ru.astrainteractive.astrarating.integration.papi.coloring

import kotlin.test.Test
import kotlin.test.assertNull

internal class ColoringUtilTest {
    @Test
    fun GIVEN_no_colorings_WHEN_coloring_is_chosen_THEN_none_is_returned() {
        val coloring = ColoringUtil.findColoringByRating(colorings = emptyList(), rating = 0)

        assertNull(coloring)
    }

    @Test
    fun GIVEN_colorings_only_for_positive_and_negative_WHEN_coloring_for_zero_is_chosen_THEN_none_is_returned() {
        val colorings = listOf(
            Coloring.Less(value = 0, color = "#FF0000"),
            Coloring.More(value = 0, color = "#00FF00")
        )

        val coloring = ColoringUtil.findColoringByRating(colorings = colorings, rating = 0)

        assertNull(coloring)
    }
}
