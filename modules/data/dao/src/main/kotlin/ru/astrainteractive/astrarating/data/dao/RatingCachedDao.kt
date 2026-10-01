package ru.astrainteractive.astrarating.data.dao

import java.util.UUID

/**
 * Api for cached rating data
 */
interface RatingCachedDao {
    /**
     * @param name - name of the player
     * @param uuid - uuid of the player
     * @return rating of player or 0 if it's not cached
     */
    fun getPlayerRating(name: String, uuid: UUID): Int

    /**
     * Loads the rating of an online player and keeps it until [release], so reading it never misses the cache.
     * A player who is not kept is dropped after a short idle time.
     */
    fun keep(uuid: UUID)

    fun release(uuid: UUID)

    fun clear()
}
