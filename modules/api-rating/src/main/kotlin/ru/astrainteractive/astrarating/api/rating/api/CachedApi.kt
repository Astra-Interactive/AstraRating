package ru.astrainteractive.astrarating.api.rating.api

import java.util.UUID

/**
 * Api for cached rating data
 */
interface CachedApi {
    /**
     * @param name - name of the player
     * @param uuid - uuid of the player
     * @return rating of player or 0 if it's not cached
     */
    fun getPlayerRating(name: String, uuid: UUID): Int
}
