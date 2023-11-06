package ru.astrainteractive.astrarating.event

import ru.astrainteractive.astrarating.event.di.EventDependencies

class EventManager(
    module: EventDependencies
) {
    init {
        KillEventListener(module)
    }
}
