package com.astrainteractive.astrarating.utils

class CachedData<KEY, VALUE>(
    val default: VALUE,
    val delay: Long = 10_000L,
    val resetEvery: Long = 50_000L
) {
    private var previousResetMillis = System.currentTimeMillis()
    private fun clearOld() {
        if (System.currentTimeMillis() - previousResetMillis < resetEvery)
            return
        else map.filter { System.currentTimeMillis() - it.value.lastUpdated > resetEvery }
            .keys.let(map.keys::removeAll)
        previousResetMillis = System.currentTimeMillis()
    }

    operator fun get(key: KEY): VALUE {
        clearOld()
        return map[key]?.value ?: default
    }

    operator fun set(key: KEY, value: VALUE) {
        map[key] = Data(System.currentTimeMillis(), value)
    }

    fun getOrUpdate(key: KEY, block: () -> VALUE): VALUE? {
        map[key]?.let { data ->
            if (System.currentTimeMillis() - data.lastUpdated < delay)
                return data.value
        }
        return block().also {
            set(key, it)
        }
    }

    private val map: HashMap<KEY, Data<VALUE>> = HashMap()

    private data class Data<VALUE>(
        val lastUpdated: Long,
        val value: VALUE
    )

}