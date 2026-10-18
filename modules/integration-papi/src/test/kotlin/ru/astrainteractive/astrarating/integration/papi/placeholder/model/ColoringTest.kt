@file:Suppress("FunctionNaming")

package ru.astrainteractive.astrarating.integration.papi.placeholder.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class ColoringTest {
    private val colorings = listOf(
        Coloring.Less(-10),
        Coloring.Less(-5),
        Coloring.Less(0),
        Coloring.Equals(0),
        Coloring.More(0),
        Coloring.More(5),
        Coloring.More(10)
    )

    private fun valueFor(rating: Int): Int? = colorings.findByRating(rating)?.value

    @Test
    fun GIVEN_negative_rating_WHEN_coloring_is_chosen_THEN_the_tightest_less_threshold_wins() {
        assertEquals(-10, valueFor(-11))
        assertEquals(-5, valueFor(-10))
        assertEquals(-5, valueFor(-9))
        assertEquals(0, valueFor(-5))
        assertEquals(0, valueFor(-4))
    }

    @Test
    fun GIVEN_zero_rating_WHEN_coloring_is_chosen_THEN_equals_threshold_wins() {
        assertEquals(Coloring.Equals(0), colorings.findByRating(0))
    }

    @Test
    fun GIVEN_positive_rating_WHEN_coloring_is_chosen_THEN_the_highest_passed_more_threshold_wins() {
        assertEquals(0, valueFor(1))
        assertEquals(0, valueFor(5))
        assertEquals(5, valueFor(6))
        assertEquals(5, valueFor(10))
        assertEquals(10, valueFor(11))
    }

    @Test
    fun GIVEN_no_colorings_WHEN_coloring_is_chosen_THEN_none_is_returned() {
        assertNull(emptyList<Coloring>().findByRating(0))
    }

    @Test
    fun GIVEN_colorings_only_for_positive_and_negative_WHEN_coloring_for_zero_is_chosen_THEN_none_is_returned() {
        val colorings = listOf(
            Coloring.Less(value = 0, color = "#FF0000"),
            Coloring.More(value = 0, color = "#00FF00")
        )

        assertNull(colorings.findByRating(0))
    }
}
