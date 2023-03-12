package com.astrainteractive.astrarating.modules

import ru.astrainteractive.astralibs.di.module
import ru.astrainteractive.astralibs.utils.encoding.BukkitIOStreamProvider
import ru.astrainteractive.astralibs.utils.encoding.Serializer

object Modules {
    val bukkitSerializer = module {
        Serializer(BukkitIOStreamProvider)
    }
}