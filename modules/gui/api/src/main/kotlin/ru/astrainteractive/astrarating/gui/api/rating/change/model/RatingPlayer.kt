package ru.astrainteractive.astrarating.gui.api.rating.change.model

import ru.astrainteractive.astralibs.server.permission.KPermissible
import ru.astrainteractive.astrarating.core.settings.AstraRatingConfig
import ru.astrainteractive.astrarating.core.settings.AstraRatingPermission
import ru.astrainteractive.astrarating.data.dao.rating.model.NewUser
import java.util.UUID

data class RatingPlayer(
    val uuid: UUID,
    val name: String,
    val permissible: KPermissible?,
    val hasPlayedBefore: Boolean
) {
    fun canVote(): Boolean = permissible?.hasPermission(AstraRatingPermission.Vote) ?: false

    fun isSamePlayer(other: RatingPlayer): Boolean = uuid == other.uuid

    fun hasVotesLeftToday(todayVotes: Long, config: AstraRatingConfig): Boolean {
        val maxVotes = permissible?.maxPermissionSize(AstraRatingPermission.MaxRatePerDay) ?: config.maxRatingPerDay
        return todayVotes < maxVotes
    }

    fun hasVotesLeftOnPlayerToday(votesOnPlayer: Long, config: AstraRatingConfig): Boolean {
        val maxVotes = permissible?.maxPermissionSize(AstraRatingPermission.SinglePlayerPerDay)
            ?: config.maxRatingPerPlayer
        return votesOnPlayer < maxVotes
    }

    fun toNewUser(): NewUser = NewUser(minecraftUUID = uuid, minecraftName = name)
}
