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
        ColoringUtil.findColoringByRating(colorings, -11).also {
            assertEquals(-10, it?.value)
        }
        ColoringUtil.findColoringByRating(colorings, -10).also {
            assertEquals(-5, it?.value)
        }
        ColoringUtil.findColoringByRating(colorings, -9).also {
            assertEquals(-5, it?.value)
        }
        ColoringUtil.findColoringByRating(colorings, -5).also {
            assertEquals(0, it?.value)
        }
        ColoringUtil.findColoringByRating(colorings, -4).also {
            assertEquals(0, it?.value)
        }
        ColoringUtil.findColoringByRating(colorings, 0).also {
            assertEquals(0, it?.value)
        }
        ColoringUtil.findColoringByRating(colorings, 1).also {
            assertEquals(0, it?.value)
        }
        ColoringUtil.findColoringByRating(colorings, 5).also {
            assertEquals(0, it?.value)
        }
        ColoringUtil.findColoringByRating(colorings, 6).also {
            assertEquals(5, it?.value)
        }
        ColoringUtil.findColoringByRating(colorings, 10).also {
            assertEquals(5, it?.value)
        }
        ColoringUtil.findColoringByRating(colorings, 11).also {
            assertEquals(10, it?.value)
        }
    }
}
