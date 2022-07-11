package com.astrainteractive.astrarating.events.events

import com.astrainteractive.astralibs.events.DSLEvent
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityDamageEvent
class MultipleEventsDSL {
    val blockBreakEvent = DSLEvent.event(BlockBreakEvent::class.java) {
    }
}

