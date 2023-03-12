package com.astrainteractive.astrarating.utils

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer
import org.bukkit.Material
import org.bukkit.inventory.ItemStack


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
    val databaseConnection: Connection = Connection(),
    @SerialName("coloring")
    val coloring: List<Coloring> = emptyList(),
    val events: Events = Events()
) {
    @Serializable
    data class Coloring(
        val less: Int? = null,
        val equal: Int? = null,
        val more: Int? = null,
        val color: String
    )


    @Serializable
    class Events(
        @SerialName("kill_player")
        val killPlayer: Event = Event()
    ) {
        @Serializable
        data class Event(
            @SerialName("change_by")
            val changeBy: Int = 0,
            val enabled: Boolean = false
        )
    }

    @Serializable
    data class Connection(
        val sqlite: Boolean = true,
        val mysql: MySqlConnection? = null
    ) {
        @Serializable
        data class MySqlConnection(
            val database: String,
            val host: String,
            val port: Int,
            val username: String,
            val password: String

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
            val back: Button = Button(Material.PAPER.name),
            val prev: Button = Button(Material.PAPER.name),
            val next: Button = Button(Material.PAPER.name),
            val sort: Button = Button(Material.SUNFLOWER.name)
        ) {
            @Serializable
            data class Button(
                val material: String,
                val customModelData: Int = 0
            ) {
                fun toItemStack() = ItemStack(Material.getMaterial(material.uppercase()) ?: Material.PAPER).apply {
                    val meta = itemMeta!!
                    meta.setCustomModelData(customModelData)
                    itemMeta = meta
                }
            }
        }
    }
}
