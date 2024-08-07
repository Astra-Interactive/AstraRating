package ru.astrainteractive.astrarating.integration.papi.coloring.coloring

import ru.astrainteractive.astrarating.integration.papi.coloring.Coloring
import ru.astrainteractive.astrarating.integration.papi.coloring.ColoringUtil
import kotlin.test.Test
import kotlin.test.assertEquals

class ColoringTest {
    @Test
    fun `Testing coloring`() {
        val defaultColor = "#FFFFFF"
        val colorings = buildList {
            Coloring.Less(-10, defaultColor).also(::add)
            Coloring.Less(-5, defaultColor).also(::add)
            Coloring.Less(0, defaultColor).also(::add)
            Coloring.Equals(0, defaultColor).also(::add)
            Coloring.More(0, defaultColor).also(::add)
            Coloring.More(5, defaultColor).also(::add)
            Coloring.More(10, defaultColor).also(::add)
        }
        ColoringUtil.getColoringByRating(colorings, -11).also {
            assertEquals(-10, it.value)
        }
        ColoringUtil.getColoringByRating(colorings, -10).also {
            assertEquals(-5, it.value)
        }
        ColoringUtil.getColoringByRating(colorings, -9).also {
            assertEquals(-5, it.value)
        }
        ColoringUtil.getColoringByRating(colorings, -5).also {
            assertEquals(0, it.value)
        }
        ColoringUtil.getColoringByRating(colorings, -4).also {
            assertEquals(0, it.value)
        }
        ColoringUtil.getColoringByRating(colorings, 0).also {
            assertEquals(0, it.value)
        }
        ColoringUtil.getColoringByRating(colorings, 1).also {
            assertEquals(0, it.value)
        }
        ColoringUtil.getColoringByRating(colorings, 5).also {
            assertEquals(0, it.value)
        }
        ColoringUtil.getColoringByRating(colorings, 6).also {
            assertEquals(5, it.value)
        }
        ColoringUtil.getColoringByRating(colorings, 10).also {
            assertEquals(5, it.value)
        }
        ColoringUtil.getColoringByRating(colorings, 11).also {
            assertEquals(10, it.value)
        }
    }
}
