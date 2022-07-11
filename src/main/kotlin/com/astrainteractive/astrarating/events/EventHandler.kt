package com.astrainteractive.astrarating.events

import com.astrainteractive.astralibs.events.EventListener
import com.astrainteractive.astralibs.events.EventManager
import com.astrainteractive.astralibs.menu.MenuListener
import com.astrainteractive.astrarating.events.events.MultipleEventsDSL


/**
 * Handler for all your events
 */
class EventHandler : EventManager {
    override val handlers: MutableList<EventListener> = mutableListOf()
    private val handler: EventHandler = this
    val menuListener = MenuListener().apply { onEnable(handler) }

    init {
        MultipleEventsDSL()
    }
}
