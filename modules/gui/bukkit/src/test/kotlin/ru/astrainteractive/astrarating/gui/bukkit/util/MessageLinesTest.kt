@file:Suppress("FunctionNaming")

package ru.astrainteractive.astrarating.gui.bukkit.util

import kotlin.test.Test
import kotlin.test.assertEquals

class MessageLinesTest {
    @Test
    fun GIVEN_cut_words_WHEN_message_is_split_THEN_lines_have_threshold_length() {
        val lines = splitMessageLines(text = "abcdefgh", threshold = 3, cutWords = true)

        assertEquals(listOf("abc", "def", "gh"), lines)
    }

    @Test
    fun GIVEN_whole_words_WHEN_message_is_split_THEN_words_are_kept_together() {
        val lines = splitMessageLines(text = "one two three four", threshold = 9, cutWords = false)

        assertEquals(listOf("one two", "three four"), lines)
    }

    @Test
    fun GIVEN_message_shorter_than_threshold_WHEN_split_by_words_THEN_each_word_is_a_line() {
        val lines = splitMessageLines(text = "gg wp", threshold = 10, cutWords = false)

        assertEquals(listOf("gg", "wp"), lines)
    }
}
