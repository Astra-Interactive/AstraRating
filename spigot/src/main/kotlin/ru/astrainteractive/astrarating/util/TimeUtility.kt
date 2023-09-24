package ru.astrainteractive.astrarating.util

import java.text.SimpleDateFormat
import java.util.Date

object TimeUtility {
    fun formatToString(time: Long, format: String): String? {
        return SimpleDateFormat(format).format(Date(time))
    }
}
