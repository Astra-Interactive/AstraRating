package com.astrainteractive.astrarating.modules

import com.astrainteractive.astrarating.AstraRating
import org.bstats.bukkit.Metrics
import ru.astrainteractive.astralibs.di.Module

object BStats : Module<Metrics>() {
    override fun initializer(): Metrics {
        return Metrics(AstraRating.instance, 15801)
    }
}