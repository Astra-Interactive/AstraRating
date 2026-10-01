@file:Suppress("FunctionNaming")

package ru.astrainteractive.astrarating.integration.papi

import kotlinx.coroutines.test.TestScope
import ru.astrainteractive.astrarating.data.dao.fake.FakeRatingCachedDao
import ru.astrainteractive.astrarating.integration.papi.di.PapiDependencies
import ru.astrainteractive.astrarating.integration.papi.model.PapiConfig
import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class RatingPAPIExpansionTest {
    @Test
    fun GIVEN_plugin_registered_expansion_WHEN_placeholderapi_reloads_THEN_expansion_is_kept() {
        val expansion = RatingPAPIExpansion(
            dependencies = PapiDependencies.Default(
                ratingCachedDao = FakeRatingCachedDao(ratings = emptyMap()),
                getPapiConfiguration = { PapiConfig(colorings = emptyList()) },
                scope = TestScope()
            )
        )

        assertTrue(expansion.persist())
    }

    @Test
    fun GIVEN_no_player_WHEN_rating_is_requested_THEN_placeholder_is_left_unparsed() {
        val expansion = RatingPAPIExpansion(
            dependencies = PapiDependencies.Default(
                ratingCachedDao = FakeRatingCachedDao(ratings = emptyMap()),
                getPapiConfiguration = { PapiConfig(colorings = emptyList()) },
                scope = TestScope()
            )
        )

        assertNull(expansion.onRequest(player = null, params = "rating"))
    }
}
