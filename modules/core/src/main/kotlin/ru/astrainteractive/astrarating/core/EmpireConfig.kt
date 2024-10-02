package ru.astrainteractive.astrarating.core

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.astrainteractive.astralibs.exposed.model.DatabaseConfiguration

/**
 * Example config file with 3 types of initialization
 */
@Serializable
data class EmpireConfig(
    // Maximum rating player can give players in total
    // Can be overwritten in LuckPerms
    val maxRatingPerDay: Int = 5,
    // Maximum rating player can give another player per day
    // Can be overwritten in LuckPerms
    val maxRatingPerPlayer: Int = 1,
    // Check if player, which creates report need linked DiscordSRV
    val needDiscordLinked: Boolean = false,
    // Minimum time played on server required to add players rating
    val minTimeOnServer: Long = 0,
    // Minimum time on discord required to let rating on players
    val minTimeOnDiscord: Long = 0,
    val minMessageLength: Int = 5,
    val maxMessageLength: Int = 30,
    val trimMessageAfter: Int = 10,
    val cutWords: Boolean = false,
    val gui: Gui = Gui(),
    val databaseConnection: DatabaseConfiguration = DatabaseConfiguration.H2("ASTRA_RATING_RATINGS"),
    val events: Events = Events(),
    val debug: Boolean = false
) {

    @Serializable
    class Events(
        @SerialName("kill_player")
        val killPlayer: Event = Event()
    ) {
        @Serializable
        data class Event(
            @SerialName("change_by")
            val changeBy: Int = -1,
            val enabled: Boolean = false
        )
    }

    @Serializable
    data class Gui(
        val showFirstConnection: Boolean = true,
        val showLastConnection: Boolean = true,
        val showDeleteReport: Boolean = true,
        val format: String = "yyyy-MM-dd",
        val buttons: Buttons = Buttons()
    ) {
        @Serializable
        data class Buttons(
            val back: Button = Button("PAPER"),
            val prev: Button = Button("PAPER"),
            val next: Button = Button("PAPER"),
            val sort: Button = Button("SUNFLOWER")
        ) {
            @Serializable
            data class Button(
                val material: String,
                val customModelData: Int = 0
            )
        }
    }
}
