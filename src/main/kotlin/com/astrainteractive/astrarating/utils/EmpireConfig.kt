package com.astrainteractive.astrarating.utils

import ru.astrainteractive.astralibs.AstraYamlParser
import ru.astrainteractive.astralibs.Logger
import org.bukkit.Material
import org.bukkit.inventory.ItemStack


val Config: EmpireConfig
    get() = EmpireConfig.instance

/**
 * Example config file with 3 types of initialization
 */
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
    val minMessageLength:Int = 5,
    val maxMessageLength:Int = 30,
    val trimMessageAfter:Int = 10,
    val cutWords:Boolean = false,
    val gui: Gui = Gui()
) {
    data class Gui(
        val showFirstConnection: Boolean = true,
        val showLastConnection: Boolean = true,
        val showDeleteReport: Boolean = true,
        val timeFormat: String = "yyyy-MM-dd",
        val buttons: Buttons = Buttons()
    ) {
        data class Buttons(
            val back: Button = Button(Material.PAPER.name),
            val prev: Button = Button(Material.PAPER.name),
            val next: Button = Button(Material.PAPER.name),
            val sort: Button = Button(Material.SUNFLOWER.name)
        ) {
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

    companion object {
        lateinit var instance: EmpireConfig

        /**
         * If you are lazy - you can use auto parser for your config
         */
        fun create(): EmpireConfig {
            val config = try {
                AstraYamlParser.fileConfigurationToClass<EmpireConfig>(Files.configFile.fileConfiguration) ?: EmpireConfig()
            } catch (e: java.lang.Exception) {
                Logger.error("Could not load config.yml check for errors. Loaded default configs", "Config")
                EmpireConfig()
            }
            instance = config
            return config
        }
    }
}
