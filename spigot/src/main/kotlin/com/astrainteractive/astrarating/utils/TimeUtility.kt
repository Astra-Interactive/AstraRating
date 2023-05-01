package com.astrainteractive.astrarating.utils

import com.astrainteractive.astrarating.modules.impl.RootModuleImpl
import java.text.SimpleDateFormat
import java.util.*

object TimeUtility {
    fun formatToString(time: Long, format: String = RootModuleImpl.config.value.gui.format): String? {
        return SimpleDateFormat(format).format(Date(time))
    }
}
