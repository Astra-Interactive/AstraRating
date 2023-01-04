package com.astrainteractive.astrarating.utils

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.utils.IPermission

/**
 * Permission class.
 *
 * All permission should be stored in companion object
 */
sealed class AstraPermission(override val value: String): IPermission {
    object Reload : AstraPermission("astra_rating.reload")
    object MaxRatePerDay : AstraPermission("astra_rating.max_rate_per_day")
    object SinglePlayerPerDay : AstraPermission("astra_rating.single_player_rate_per_day")
    object Vote : AstraPermission("astra_rating.vote")
    object SeeRecords : AstraPermission("astra_rating.see_records")
    object MergeRecords : AstraPermission("astra_rating.merge_records")
    object DeleteReport : AstraPermission("delete_report.vote")

}