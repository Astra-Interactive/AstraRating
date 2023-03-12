package com.astrainteractive.astrarating.events

import com.astrainteractive.astrarating.domain.api.IRatingAPI
import com.astrainteractive.astrarating.utils.EmpireConfig
import com.astrainteractive.astrarating.utils.PluginTranslation
import ru.astrainteractive.astralibs.di.IDependency

class EventManager(
    configDependency: IDependency<EmpireConfig>,
    apiDependency: IDependency<IRatingAPI>,
    translationDependency: IDependency<PluginTranslation>
) {
    init {
        KillEventListener(
            configDependency = configDependency,
            apiDependency = apiDependency,
            translationDependency = translationDependency
        )
    }
}