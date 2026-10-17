package ru.astrainteractive.astrarating.core.settings.util

import java.io.File

private val snakeCaseKeys = mapOf(
    "maxRatingPerDay:" to "max_rating_per_day:",
    "maxRatingPerPlayer:" to "max_rating_per_player:",
    "needDiscordLinked:" to "need_discord_linked:",
    "minTimeOnServer:" to "min_time_on_server:",
    "minTimeOnDiscord:" to "min_time_on_discord:",
    "minMessageLength:" to "min_message_length:",
    "maxMessageLength:" to "max_message_length:",
    "trimMessageAfter:" to "trim_message_after:",
    "cutWords:" to "cut_words:",
    "showFirstConnection:" to "show_first_connection:",
    "showLastConnection:" to "show_last_connection:",
    "showDeleteReport:" to "show_delete_report:",
    "customModelData:" to "custom_model_data:",
)

internal fun migrateConfigToSnakeCase(file: File) {
    if (!file.exists()) return
    val content = file.readText()
    val migrated = snakeCaseKeys.entries.fold(content) { text, key -> text.replace(key.key, key.value) }
    if (migrated != content) file.writeText(migrated)
}
