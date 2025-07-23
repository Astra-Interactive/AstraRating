package ru.astrainteractive.astrarating.core.util

import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import kotlin.reflect.KProperty

@Deprecated("Replace with kstorage function")
operator fun <T> CachedKrate<T>.getValue(thisRef: Nothing?, property: KProperty<*>): T {
    return this.getValue()
}
