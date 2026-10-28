@file:Suppress("ForbiddenImport")

package ru.astrainteractive.astrarating.gui.bukkit.util

import java.text.SimpleDateFormat
import java.util.Date
import kotlin.time.Instant

internal fun formatDate(instant: Instant, format: String): String {
    return SimpleDateFormat(format).format(Date(instant.toEpochMilliseconds()))
}
