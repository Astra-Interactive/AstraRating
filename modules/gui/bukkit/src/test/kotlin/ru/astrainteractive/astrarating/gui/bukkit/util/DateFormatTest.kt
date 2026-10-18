@file:Suppress("FunctionNaming")

package ru.astrainteractive.astrarating.gui.bukkit.util

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant

class DateFormatTest {
    @Test
    fun GIVEN_instant_WHEN_formatted_with_pattern_THEN_pattern_is_applied() {
        val instant = Instant.parse("2024-06-15T12:00:00Z")

        assertEquals("2024-06", formatDate(instant, "yyyy-MM"))
    }
}
