package com.astrainteractive.astrarating.events

import com.astrainteractive.astrarating.events.di.EventModule

class EventManager(
    module: EventModule
) {
    init {
        KillEventListener(module)
    }
}
