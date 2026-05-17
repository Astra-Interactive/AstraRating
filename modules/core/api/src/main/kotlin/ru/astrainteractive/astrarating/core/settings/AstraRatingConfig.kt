package ru.astrainteractive.astrarating.core.settings

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Example config file with 3 types of initialization
 */
@Serializable
data class AstraRatingConfig(
    // Maximum rating player can give players in total
    // Can be overwritten in LuckPerms
    @SerialName("max_rating_per_day")
    val maxRatingPerDay: Int = 5,
    // Maximum rating player can give another player per day
    // Can be overwritten in LuckPerms
    @SerialName("max_rating_per_player")
    val maxRatingPerPlayer: Int = 1,
    // Check if player, which creates report need linked DiscordSRV
    @SerialName("need_discord_linked")
    val needDiscordLinked: Boolean = false,
    // Minimum time played on server required to add players rating
    @SerialName("min_time_on_server")
    val minTimeOnServer: Long = 0,
    // Minimum time on discord required to let rating on players
    @SerialName("min_time_on_discord")
    val minTimeOnDiscord: Long = 0,
    @SerialName("min_message_length")
    val minMessageLength: Int = 5,
    @SerialName("max_message_length")
    val maxMessageLength: Int = 30,
    @SerialName("trim_message_after")
    val trimMessageAfter: Int = 10,
    @SerialName("cut_words")
    val cutWords: Boolean = false,
    val gui: Gui = Gui(),
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
        @SerialName("show_first_connection")
        val showFirstConnection: Boolean = true,
        @SerialName("show_last_connection")
        val showLastConnection: Boolean = true,
        @SerialName("show_delete_report")
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
                @SerialName("custom_model_data")
                val customModelData: Int = 0
            )
        }
    }
}
