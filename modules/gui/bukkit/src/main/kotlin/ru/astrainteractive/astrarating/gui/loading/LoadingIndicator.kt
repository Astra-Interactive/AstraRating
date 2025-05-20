package ru.astrainteractive.astrarating.gui.loading

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.menu.core.Menu
import ru.astrainteractive.astrarating.core.PluginTranslation

internal class LoadingIndicator(
    private val menu: Menu,
    private val translation: PluginTranslation,
    private val translationContext: KyoriComponentSerializer
) {
    private var job: Job? = null
    private val items = listOf(
        Material.BLACK_STAINED_GLASS,
        Material.BLUE_STAINED_GLASS,
        Material.BROWN_STAINED_GLASS,
        Material.CYAN_STAINED_GLASS,
        Material.GREEN_STAINED_GLASS,
        Material.LIME_STAINED_GLASS,
        Material.RED_STAINED_GLASS,
    )
    private var offset = 0

    private fun render() {
        val start = 2 * 9
        val max = 9 * 2
        for (i in start until (start + max)) {
            val material = items[(i + offset) % items.size]
            val itemStack = ItemStack(material)
            itemStack.editMeta {
                val message = translationContext.toComponent(translation.loading)
                it.displayName(message)
            }
            menu.inventory.setItem(i, itemStack)
        }
        offset = (offset + 1)
        if (offset >= items.size) offset = 0
    }

    suspend fun stop() {
        job?.cancelAndJoin()
        job = null
    }

    suspend fun display(scope: CoroutineScope) {
        stop()
        job = scope.launch {
            while (isActive) {
                render()
                delay(100L)
            }
        }
    }
}
