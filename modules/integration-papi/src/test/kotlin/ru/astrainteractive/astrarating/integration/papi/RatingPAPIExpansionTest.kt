@file:Suppress("FunctionNaming")

package ru.astrainteractive.astrarating.integration.papi

import kotlinx.coroutines.test.TestScope
import ru.astrainteractive.astrarating.data.dao.fake.FakeRatingCachedDao
import ru.astrainteractive.astrarating.integration.papi.di.PapiDependencies
import ru.astrainteractive.astrarating.integration.papi.model.PapiConfig
import kotlin.test.Test
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
}
