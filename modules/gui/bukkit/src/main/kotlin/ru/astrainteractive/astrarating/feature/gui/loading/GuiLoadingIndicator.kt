package ru.astrainteractive.astrarating.feature.gui.loading

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.menu.core.Menu
import ru.astrainteractive.astrarating.core.settings.AstraRatingTranslation

internal class GuiLoadingIndicator(
    private val menu: Menu,
    private val translation: AstraRatingTranslation,
    kyori: KyoriComponentSerializer
) : KyoriComponentSerializer by kyori {

    private companion object {
        const val ROW_SIZE = 9
        const val START_ROW = 2
        const val ROWS = 2

        const val FRAME_DELAY = 100L
    }

    private val startSlot = START_ROW * ROW_SIZE
    private val slotCount = ROWS * ROW_SIZE
    private val slots = (startSlot until startSlot + slotCount)

    private val mutex = Mutex()
    private var job: Job? = null

    private val materials = listOf(
        Material.BLACK_STAINED_GLASS,
        Material.BLUE_STAINED_GLASS,
        Material.BROWN_STAINED_GLASS,
        Material.CYAN_STAINED_GLASS,
        Material.GREEN_STAINED_GLASS,
        Material.LIME_STAINED_GLASS,
        Material.RED_STAINED_GLASS,
    )

    private val frames: List<List<ItemStack>> = let {
        materials.indices.map { offset ->
            slots.map { slotIndex ->
                val material = materials[(slotIndex + offset) % materials.size]
                createItem(material)
            }
        }
    }

    private fun createItem(material: Material): ItemStack {
        return ItemStack(material).apply {
            editMeta { itemMeta ->
                itemMeta.displayName(translation.gui.loading.component)
            }
        }
    }

    private fun render(frame: List<ItemStack>) {
        slots.forEachIndexed { i, slot ->
            menu.inventory.setItem(slot, frame[i])
        }
    }

    suspend fun display(scope: CoroutineScope) {
        stop()
        mutex.withLock {
            job = scope.launch {
                while (isActive) {
                    var index = 0

                    while (isActive) {
                        render(frames[index])

                        index = (index + 1) % frames.size
                        delay(FRAME_DELAY)
                    }
                }
            }
        }
    }

    suspend fun stop() {
        mutex.withLock {
            job?.cancelAndJoin()
            job = null
        }
    }
}
