@file:Suppress("FunctionNaming")

package ru.astrainteractive.astrarating.gui.api.util

import kotlin.test.Test
import kotlin.test.assertEquals

class SortCycleExtTest {
    private val sorts = listOf("rating", "player", "date")

    @Test
    fun GIVEN_current_sort_WHEN_next_is_chosen_THEN_following_sort_is_returned() {
        assertEquals("player", sorts.nextAfter("rating", backwards = false))
    }

    @Test
    fun GIVEN_last_sort_WHEN_next_is_chosen_THEN_first_sort_is_returned() {
        assertEquals("rating", sorts.nextAfter("date", backwards = false))
    }

    @Test
    fun GIVEN_first_sort_WHEN_previous_is_chosen_THEN_last_sort_is_returned() {
        assertEquals("date", sorts.nextAfter("rating", backwards = true))
    }

    @Test
    fun GIVEN_unknown_sort_WHEN_next_is_chosen_THEN_first_sort_is_returned() {
        assertEquals("rating", sorts.nextAfter("name", backwards = true))
    }
}
