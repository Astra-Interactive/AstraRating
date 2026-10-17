package ru.astrainteractive.astrarating.data.dao.rating.api

import ru.astrainteractive.astrarating.data.dao.rating.model.RatedUser

interface RatedUsersCache {
    suspend fun fetchUsersTotalRating(): List<RatedUser>

    fun clear()
}
