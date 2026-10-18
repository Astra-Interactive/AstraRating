@file:Suppress("FunctionNaming")

package ru.astrainteractive.astrarating.gui.bukkit.util

import ru.astrainteractive.astrarating.core.settings.model.AstraRatingTranslation
import ru.astrainteractive.astrarating.gui.api.rating.players.model.UsersRatingsSort
import kotlin.test.Test
import kotlin.test.assertEquals

class UsersRatingsSortExtTest {
    private val translation = AstraRatingTranslation()

    @Test
    fun GIVEN_total_rating_sort_WHEN_shown_THEN_rating_label_is_used() {
        assertEquals(translation.sort.rating, UsersRatingsSort.TotalRating(isAsc = false).toText(translation))
    }
}
