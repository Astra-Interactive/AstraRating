package com.astrainteractive.astrarating.integrations.papi.coloring

import com.astrainteractive.astrarating.plugin.EmpireConfig
import ru.astrainteractive.klibs.mikro.core.domain.Mapper

object ColoringMapper : Mapper<EmpireConfig.Coloring, Coloring> {
    override fun toDTO(it: EmpireConfig.Coloring): Coloring {
        val coloring = it.equal?.let { value ->
            Coloring.Equals(value, it.color)
        } ?: it.more?.let { value ->
            Coloring.More(value, it.color)
        } ?: it.less?.let { value ->
            Coloring.Less(value, it.color)
        }
        return checkNotNull(coloring) { "At lease on less/more/equal should be specified in config!" }
    }

    override fun fromDTO(it: Coloring): EmpireConfig.Coloring {
        return when (it) {
            is Coloring.Equals -> EmpireConfig.Coloring(equal = it.value, color = it.color)
            is Coloring.Less -> EmpireConfig.Coloring(less = it.value, color = it.color)
            is Coloring.More -> EmpireConfig.Coloring(more = it.value, color = it.color)
        }
    }
}
