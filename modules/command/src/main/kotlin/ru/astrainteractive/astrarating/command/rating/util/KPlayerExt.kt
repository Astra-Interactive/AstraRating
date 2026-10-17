package ru.astrainteractive.astrarating.command.rating.util

import ru.astrainteractive.astralibs.server.permission.KPermissible
import ru.astrainteractive.astralibs.server.player.KPlayer
import ru.astrainteractive.astralibs.server.player.OnlineKPlayer
import ru.astrainteractive.astrarating.gui.api.rating.change.model.RatingPlayer
import ru.astrainteractive.klibs.mikro.core.util.tryCast

internal fun KPlayer.toRatingPlayer(): RatingPlayer? {
    return RatingPlayer(
        uuid = uuid,
        name = name ?: return null,
        permissible = tryCast<KPermissible>(),
        hasPlayedBefore = hasPlayedBefore()
    )
}

internal fun OnlineKPlayer.toRatingPlayer(): RatingPlayer {
    return RatingPlayer(
        uuid = uuid,
        name = name,
        permissible = this,
        hasPlayedBefore = hasPlayedBefore()
    )
}
