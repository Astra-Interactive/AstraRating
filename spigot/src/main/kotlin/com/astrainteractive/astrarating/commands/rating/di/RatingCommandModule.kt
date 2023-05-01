package com.astrainteractive.astrarating.commands.rating.di

import com.astrainteractive.astrarating.commands.rating.RatingCommandController
import com.astrainteractive.astrarating.plugin.PluginTranslation
import kotlinx.coroutines.CoroutineScope
import ru.astrainteractive.astralibs.Dependency
import ru.astrainteractive.astralibs.async.BukkitDispatchers

interface RatingCommandModule {
    val dispatchers: Dependency<BukkitDispatchers>
    val scope: Dependency<CoroutineScope>
    val ratingCommandController: Dependency<RatingCommandController>
    val translation: Dependency<PluginTranslation>
}
