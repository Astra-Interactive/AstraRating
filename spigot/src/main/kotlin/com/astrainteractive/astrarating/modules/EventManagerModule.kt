package com.astrainteractive.astrarating.modules

import com.astrainteractive.astrarating.events.EventManager
import ru.astrainteractive.astralibs.di.Module

object EventManagerModule : Module<EventManager>() {
    override fun initializer(): EventManager {
        return EventManager(
            configDependency = ConfigProvider,
            apiDependency = DatabaseApiModule,
            translationDependency = TranslationProvider
        )
    }
}