package ru.astrainteractive.astrarating.command.rating

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.util.uuid
import ru.astrainteractive.astrarating.command.di.CommandsDependencies
import ru.astrainteractive.astrarating.command.rating.exception.ValidationException
import ru.astrainteractive.astrarating.dto.RatingType
import ru.astrainteractive.astrarating.dto.UserDTO
import ru.astrainteractive.astrarating.model.UserModel
import ru.astrainteractive.astrarating.plugin.RatingPermission

class RatingCommandController(
    module: CommandsDependencies
) : CommandsDependencies by module {

    @Throws(ValidationException::class)
    @Suppress("CyclomaticComplexMethod")
    suspend fun addRating(
        ratingCreator: CommandSender,
        message: String,
        ratedPlayer: OfflinePlayer?,
        rating: Int,
        typeDTO: RatingType
    ): Result<*> = kotlin.runCatching {
        if (ratingCreator !is Player) throw ValidationException.OnlyPlayerCommand(ratingCreator)
        val hasVotePermission = permissionManager.hasPermission(ratingCreator.uniqueId, RatingPermission.Vote)
        if (!hasVotePermission) throw ValidationException.NoPermission(ratingCreator)

        if (ratedPlayer == null || ratedPlayer.firstPlayed == 0L) {
            throw ValidationException.PlayerNotExists(ratingCreator)
        }

        if (ratedPlayer == ratingCreator) throw ValidationException.SamePlayer(ratingCreator)

        if (System.currentTimeMillis() - ratingCreator.firstPlayed < config.minTimeOnServer) {
            throw ValidationException.NotEnoughOnServer(ratingCreator)
        }

        val maxVotesPerDay = permissionManager.maxPermissionSize(
            ratingCreator.uniqueId,
            RatingPermission.MaxRatePerDay
        ) ?: config.maxRatingPerDay
        val maxVotePerPlayer = permissionManager.maxPermissionSize(
            ratingCreator.uniqueId,
            RatingPermission.SinglePlayerPerDay
        ) ?: config.maxRatingPerPlayer

//        if (config.needDiscordLinked) {
//            val discordMember = getLinkedDiscordID(ratingCreator)
//                ?.let { getDiscordMember(it) }
//                ?: throw ValidationException.DiscordNotLinked(ratingCreator)
//
//            val wasInDiscordSince = discordMember.timeJoined.toInstant().toEpochMilli()
//            if (System.currentTimeMillis() - wasInDiscordSince < config.minTimeOnDiscord)
//                throw ValidationException.NotEnoughOnDiscord(ratingCreator)
//        }
        translationContext.toComponent(translation.pleaseWait).let { message ->
            ratingCreator.sendMessage(message)
        }

        val todayVotedAmount = dbApi.countPlayerTotalDayRated(ratingCreator.name).getOrNull() ?: 0
        val votedOnPlayerAmount =
            dbApi.countPlayerOnPlayerDayRated(ratingCreator.name, ratedPlayer.name ?: "NULL").getOrNull() ?: 0

        if (todayVotedAmount > maxVotesPerDay) {
            throw ValidationException.AlreadyMaxDayVotes(ratingCreator)
        }

        if (votedOnPlayerAmount > maxVotePerPlayer) {
            throw ValidationException.AlreadyMaxVotesOnPlayer(ratingCreator)
        }

        if (message.length < config.minMessageLength || message.length > config.maxMessageLength) {
            throw ValidationException.WrongMessageLength(ratingCreator)
        }

        val playerCreatedID = insertUseCase(UserModel(ratingCreator.uniqueId, ratingCreator.name))
        val playerReportedID =
            insertUseCase(UserModel(ratedPlayer.uniqueId, ratedPlayer?.name ?: "NULL"))
        if (playerCreatedID == null || playerReportedID == null) throw ValidationException.DBException(ratingCreator)
        dbApi.insertUserRating(
            reporter = UserDTO(
                id = playerCreatedID,
                minecraftUUID = ratingCreator.uuid,
                minecraftName = ratingCreator.name ?: "-"
            ),
            reported = UserDTO(
                id = playerReportedID,
                minecraftUUID = ratedPlayer.uuid,
                minecraftName = ratedPlayer.name ?: "-"
            ),
            message = message,
            type = typeDTO,
            ratingValue = rating
        )

        if (rating > 0) {
            val message = translationContext.toComponent(
                translation.likedUser.replace("%player%", ratedPlayer.name ?: "-")
            )
            ratingCreator.sendMessage(message)
        } else {
            val message = translationContext.toComponent(
                translation.dislikedUser.replace("%player%", ratedPlayer.name ?: "-")
            )
            ratingCreator.sendMessage(message)
        }
    }

    fun rating(sender: CommandSender) {
        scope.launch(dispatchers.BukkitAsync) {
            val inventory = ratingsGUIFactory(sender as Player).create()
            withContext(dispatchers.BukkitMain) {
                inventory.open()
            }
        }
    }
}
