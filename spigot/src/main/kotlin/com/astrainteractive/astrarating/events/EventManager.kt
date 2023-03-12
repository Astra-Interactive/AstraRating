package com.astrainteractive.astrarating.events

import com.astrainteractive.astrarating.domain.api.RatingDBApi
import com.astrainteractive.astrarating.utils.EmpireConfig
import com.astrainteractive.astrarating.utils.PluginTranslation
import ru.astrainteractive.astralibs.di.Dependency

class EventManager(
    configDependency: Dependency<EmpireConfig>,
    apiDependency: Dependency<RatingDBApi>,
    translationDependency: Dependency<PluginTranslation>
) {
    init {
        KillEventListener(
            configDependency = configDependency,
            apiDependency = apiDependency,
            translationDependency = translationDependency
        )
    }
}