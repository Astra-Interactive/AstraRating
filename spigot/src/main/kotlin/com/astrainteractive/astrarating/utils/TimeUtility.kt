package com.astrainteractive.astrarating.utils

import com.astrainteractive.astrarating.modules.ServiceLocator
import java.text.SimpleDateFormat
import java.util.*

object TimeUtility {
    fun formatToString(time: Long, format: String = ServiceLocator.config.value.gui.format): String? {
        return SimpleDateFormat(format).format(Date(time))
    }
}