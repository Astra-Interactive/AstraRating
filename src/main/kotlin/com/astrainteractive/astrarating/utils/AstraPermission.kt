package com.astrainteractive.astrarating.utils

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * Permission class.
 *
 * All permission should be stored in companion object
 */
sealed class AstraPermission(val value: String) {
    object Reload : AstraPermission("astra_rating.reload")
    object MaxRatePerDay : AstraPermission("astra_rating.max_rate_per_day")
    object SinglePlayerPerDay : AstraPermission("astra_rating.single_player_rate_per_day")
    object Vote : AstraPermission("astra_rating.vote")
    object SeeRecords : AstraPermission("astra_rating.see_records")
    object MergeRecords : AstraPermission("astra_rating.merge_records")
    object DeleteReport : AstraPermission("delete_report.vote")

    fun hasPermission(player: CommandSender) = player.hasPermission(value)
    fun permissionSize(player: Player) = player.effectivePermissions
        .firstOrNull { it.permission.startsWith(value) }
        ?.permission
        ?.replace("$value.", "")
        ?.toIntOrNull()
}