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
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.menu.core.Menu
import ru.astrainteractive.astralibs.menu.core.setInventorySlot
import ru.astrainteractive.astralibs.menu.layout.slotInventoryLayout
import ru.astrainteractive.astralibs.menu.slot.InventorySlot
import ru.astrainteractive.astralibs.menu.slot.editMeta
import ru.astrainteractive.astralibs.menu.slot.setIndex
import ru.astrainteractive.astralibs.menu.slot.setMaterial
import ru.astrainteractive.astrarating.core.settings.AstraRatingTranslation
import kotlin.time.Duration.Companion.milliseconds

internal class GuiLoadingIndicator(
    private val menu: Menu,
    private val translation: AstraRatingTranslation,
    kyori: KyoriComponentSerializer
) : KyoriComponentSerializer by kyori {

    private enum class SlotKey { EMPTY, LOADING }

    @Suppress("MagicNumber")
    private val layout = slotInventoryLayout<SlotKey> {
        row(9, SlotKey.LOADING)
        repeat(3) {
            row(
                SlotKey.LOADING,
                SlotKey.EMPTY,
                SlotKey.EMPTY,
                SlotKey.EMPTY,
                SlotKey.EMPTY,
                SlotKey.EMPTY,
                SlotKey.EMPTY,
                SlotKey.EMPTY,
                SlotKey.LOADING
            )
        }
        row(9, SlotKey.LOADING)
    }

    private val slots = layout.indicesOf(SlotKey.LOADING)

    private val materials = listOf(
        Material.BLACK_STAINED_GLASS,
        Material.BLUE_STAINED_GLASS,
        Material.BROWN_STAINED_GLASS,
        Material.CYAN_STAINED_GLASS,
        Material.GREEN_STAINED_GLASS,
        Material.LIME_STAINED_GLASS,
        Material.RED_STAINED_GLASS,
    )

    private val frames: List<List<InventorySlot>> = materials.indices.map { offset ->
        slots.map { slotIndex ->
            InventorySlot.Builder()
                .setMaterial(materials[(slotIndex + offset) % materials.size])
                .setIndex(slotIndex)
                .editMeta { displayName(translation.gui.loading.component) }
                .build()
        }
    }

    private val mutex = Mutex()
    private var job: Job? = null

    suspend fun display(menuScope: CoroutineScope) {
        stop()
        mutex.withLock {
            job?.cancelAndJoin()
            job = menuScope.launch {
                var index = 0
                while (isActive) {
                    menu.setInventorySlot(frames[index])
                    index = (index + 1) % frames.size
                    delay(FRAME_DELAY)
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

    companion object {
        private val FRAME_DELAY = 100L.milliseconds
    }
}
