package ru.astrainteractive.astrarating.core.util

import ru.astrainteractive.klibs.kstorage.api.cache.CacheOwner
import kotlin.reflect.KProperty

object KrateExt {
    operator fun <T> CacheOwner<T>.getValue(thisRef: Any, property: KProperty<*>): T {
        return this.cachedValue
    }
}
