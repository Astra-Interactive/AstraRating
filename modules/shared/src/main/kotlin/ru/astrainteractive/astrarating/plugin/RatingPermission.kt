package ru.astrainteractive.astrarating.plugin

import ru.astrainteractive.astralibs.permission.Permission

sealed class RatingPermission(override val value: String) : Permission {
    data object Reload : RatingPermission("astra_rating.reload")
    data object MaxRatePerDay : RatingPermission("astra_rating.max_rate_per_day")
    data object SinglePlayerPerDay : RatingPermission("astra_rating.single_player_rate_per_day")
    data object Vote : RatingPermission("astra_rating.vote")
    data object SeeRecords : RatingPermission("astra_rating.see_records")
    data object MergeRecords : RatingPermission("astra_rating.merge_records")
    data object DeleteReport : RatingPermission("delete_report.vote")
}
