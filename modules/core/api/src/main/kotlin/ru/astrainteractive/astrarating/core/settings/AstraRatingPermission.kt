package ru.astrainteractive.astrarating.core.settings

import ru.astrainteractive.astralibs.permission.Permission

sealed class AstraRatingPermission(override val value: String) : Permission {
    data object Reload : AstraRatingPermission("astra_rating.reload")
    data object MaxRatePerDay : AstraRatingPermission("astra_rating.max_rate_per_day")
    data object SinglePlayerPerDay : AstraRatingPermission("astra_rating.single_player_rate_per_day")
    data object Vote : AstraRatingPermission("astra_rating.vote")
    data object DeleteReport : AstraRatingPermission("delete_report.vote")
}
