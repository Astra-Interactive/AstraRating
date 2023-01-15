package com.astrainteractive.astrarating.utils

import kotlin.reflect.KProperty

abstract class Singleton<T : Any> {
    lateinit var instance: T
}
private operator fun <T : Any> Singleton<T>.getValue(t: T?, property: KProperty<*>): T {
    return instance
}