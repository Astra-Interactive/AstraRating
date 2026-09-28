@file:Suppress("FunctionNaming")

package ru.astrainteractive.astrarating.core.settings

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import ru.astrainteractive.astralibs.localization.component.LocalizableComponent
import ru.astrainteractive.astralibs.localization.locale.MinecraftLocales
import kotlin.test.Test
import kotlin.test.assertEquals

class AstraRatingTranslationTest {
    private val translation = AstraRatingTranslation()

    private fun plainText(text: LocalizableComponent): String {
        return PlainTextComponentSerializer.plainText().serialize(text.toComponent(MinecraftLocales.EN_US))
    }

    /** Flattens a tree to `§` codes, so differently nested trees with the same look compare equal. */
    private fun visualForm(text: LocalizableComponent): String {
        return LegacyComponentSerializer.legacySection().serialize(text.toComponent(MinecraftLocales.EN_US))
    }

    @Test
    fun GIVEN_sort_that_is_not_selected_WHEN_option_THEN_no_direction_is_shown() {
        val option = translation.sort.option(sort = translation.sort.date, isSelected = false, isAscending = true)

        assertEquals("Date", plainText(option))
    }

    @Test
    fun GIVEN_selected_ascending_sort_WHEN_option_THEN_down_arrow_is_shown() {
        val option = translation.sort.option(sort = translation.sort.date, isSelected = true, isAscending = true)

        assertEquals("Date ↓", plainText(option))
    }

    @Test
    fun GIVEN_selected_descending_sort_WHEN_option_THEN_up_arrow_is_shown() {
        val option = translation.sort.option(sort = translation.sort.date, isSelected = true, isAscending = false)

        assertEquals("Date ↑", plainText(option))
    }

    @Test
    fun GIVEN_positive_rating_WHEN_rating_value_THEN_it_is_green() {
        val value = translation.menu.ratingValue(isPositive = true, text = "5")

        assertEquals("§25", visualForm(value))
    }

    @Test
    fun GIVEN_negative_rating_WHEN_rating_value_THEN_it_is_red() {
        val value = translation.menu.ratingValue(isPositive = false, text = "-5")

        assertEquals("§4-5", visualForm(value))
    }

    @Test
    fun GIVEN_player_message_with_markup_WHEN_shown_as_rating_message_THEN_markup_stays_text() {
        val line = translation.menu.ratingValue(isPositive = true, text = "&c<red>hi")

        val message = translation.playerRatingsMenu.message(line)

        assertEquals("Message: &c<red>hi", plainText(message))
    }
}
