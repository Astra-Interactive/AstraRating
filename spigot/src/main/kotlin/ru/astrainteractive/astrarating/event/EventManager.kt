package ru.astrainteractive.astrarating.event

import ru.astrainteractive.astrarating.event.di.EventDependencies
import ru.astrainteractive.astrarating.event.kill.KillEventListener

class EventManager(
    module: EventDependencies
) {
    init {
        KillEventListener(module)
    }
}
