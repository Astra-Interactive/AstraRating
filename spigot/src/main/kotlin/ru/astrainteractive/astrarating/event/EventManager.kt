package ru.astrainteractive.astrarating.event

import ru.astrainteractive.astrarating.event.di.EventModule

class EventManager(
    module: EventModule
) {
    init {
        KillEventListener(module)
    }
}
