package com.astrainteractive.astrarating.utils

import com.astrainteractive.astralibs.AstraYamlParser
import com.astrainteractive.astralibs.Logger


/**
 * Example config file with 3 types of initialization
 */
data class EmpireConfig(
    // Maximum rating player can give players in total
    val maxRatingPerDay: Int = 5,
    // Maximum rating player can give another player per day
    val ratingPerDayPerPlayer: Int = 1,
    // Check if player, which creates report need linked DiscordSRV
    val needDiscordLinked: Boolean = false,
    // Minimum time played on server required to add players rating
    val minTimeOnServer: Long = 0,
    // Minimum time on discord required to let rating on players
    val minTimeOnDiscord: Long = 0,
) {
    companion object {
        lateinit var instance: EmpireConfig

        /**
         * If you are lazy - you can use auto parser for your config
         */
        fun create(): EmpireConfig {
            val config =
                AstraYamlParser.fileConfigurationToClass<EmpireConfig>(Files.configFile.getConfig())!!
            instance = config
            return config
        }
    }
}
