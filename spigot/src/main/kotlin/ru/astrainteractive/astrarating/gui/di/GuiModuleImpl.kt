package ru.astrainteractive.astrarating.gui.di

import ru.astrainteractive.astralibs.permission.BukkitPermissionManager
import ru.astrainteractive.astralibs.permission.PermissionManager
import ru.astrainteractive.astralibs.serialization.KyoriComponentSerializer
import ru.astrainteractive.astrarating.di.RootModule
import ru.astrainteractive.klibs.kdi.getValue

class GuiModuleImpl(private val rootModule: RootModule) : GuiModule {

    override val dbApi = rootModule.apiRatingModule.ratingDBApi
    override val dispatchers by rootModule.servicesModule.dispatchers
    override val translation by rootModule.servicesModule.translation
    override val config by rootModule.servicesModule.config
    override val permissionManager: PermissionManager = BukkitPermissionManager()
    override val translationContext: KyoriComponentSerializer by rootModule.servicesModule.componentSerializer
}
