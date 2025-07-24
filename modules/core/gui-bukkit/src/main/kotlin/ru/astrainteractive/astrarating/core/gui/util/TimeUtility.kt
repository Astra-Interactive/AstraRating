package ru.astrainteractive.astrarating.core.gui.util

import java.text.SimpleDateFormat
import java.util.Date

internal object TimeUtility {
    fun formatToString(time: Long, format: String): String? {
        return SimpleDateFormat(format).format(Date(time))
    }
}
