package ru.astrainteractive.astrarating.gui.bukkit.slot

import net.kyori.adventure.text.Component
import org.bukkit.OfflinePlayer
import ru.astrainteractive.astrarating.gui.bukkit.context.SlotContext
import ru.astrainteractive.astrarating.gui.bukkit.util.firstPlayedAt
import ru.astrainteractive.astrarating.gui.bukkit.util.formatDate
import ru.astrainteractive.astrarating.gui.bukkit.util.lastPlayedAt

internal fun SlotContext.connectionLines(player: OfflinePlayer): List<Component> = buildList {
    val firstPlayedAt = player.firstPlayedAt
    if (config.gui.showFirstConnection && firstPlayedAt != null) {
        val time = formatDate(firstPlayedAt, config.gui.format)
        if (time.isNotBlank()) add(translation.menu.firstConnection(time).toComponent(locale))
    }
    val lastPlayedAt = player.lastPlayedAt
    if (config.gui.showLastConnection && lastPlayedAt != null) {
        val time = formatDate(lastPlayedAt, config.gui.format)
        if (time.isNotBlank()) add(translation.menu.lastConnection(time).toComponent(locale))
    }
}
