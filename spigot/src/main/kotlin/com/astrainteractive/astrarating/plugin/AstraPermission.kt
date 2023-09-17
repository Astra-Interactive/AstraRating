package com.astrainteractive.astrarating.plugin

import ru.astrainteractive.astralibs.util.Permission

/**
 * Permission class.
 *
 * All permission should be stored in companion object
 */
sealed class AstraPermission(override val value: String) : Permission {
    data object Reload : AstraPermission("astra_rating.reload")
    data object MaxRatePerDay : AstraPermission("astra_rating.max_rate_per_day")
    data object SinglePlayerPerDay : AstraPermission("astra_rating.single_player_rate_per_day")
    data object Vote : AstraPermission("astra_rating.vote")
    data object SeeRecords : AstraPermission("astra_rating.see_records")
    data object MergeRecords : AstraPermission("astra_rating.merge_records")
    data object DeleteReport : AstraPermission("delete_report.vote")
}
