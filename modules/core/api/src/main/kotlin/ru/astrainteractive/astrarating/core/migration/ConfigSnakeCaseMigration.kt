package ru.astrainteractive.astrarating.core.migration

import java.io.File

internal object ConfigSnakeCaseMigration {

    private val replacements = mapOf(
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

    fun migrate(file: File) {
        if (!file.exists()) return
        var content = file.readText()
        val changed = replacements
            .filter { (old, _) -> content.contains(old) }
            .map { (old, new) ->
                content = content.replace(old, new)
                true
            }
            .any()
        if (!changed) return
        file.writeText(content)
    }
}
