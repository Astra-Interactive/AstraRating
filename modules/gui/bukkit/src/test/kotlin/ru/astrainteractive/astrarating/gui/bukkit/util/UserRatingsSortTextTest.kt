@file:Suppress("FunctionNaming")

package ru.astrainteractive.astrarating.gui.bukkit.util

import ru.astrainteractive.astrarating.core.settings.model.AstraRatingTranslation
import ru.astrainteractive.astrarating.gui.api.rating.player.model.UserRatingsSort
import kotlin.test.Test
import kotlin.test.assertEquals

class UserRatingsSortTextTest {
    private val translation = AstraRatingTranslation()

    @Test
    fun GIVEN_player_ratings_sorts_WHEN_shown_THEN_each_has_its_label() {
        assertEquals(translation.sort.date, selectUserRatingsSortText(UserRatingsSort.Date(isAsc = true), translation))
        assertEquals(
            translation.sort.player,
            selectUserRatingsSortText(UserRatingsSort.Player(isAsc = true), translation)
        )
        assertEquals(
            translation.sort.rating,
            selectUserRatingsSortText(UserRatingsSort.Rating(isAsc = true), translation)
        )
    }
}
