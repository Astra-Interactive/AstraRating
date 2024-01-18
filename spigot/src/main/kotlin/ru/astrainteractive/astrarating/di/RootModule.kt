package ru.astrainteractive.astrarating.di

import ru.astrainteractive.astrarating.api.rating.di.ApiRatingModule
import ru.astrainteractive.astrarating.command.di.CommandsModule
import ru.astrainteractive.astrarating.db.rating.di.DBRatingModule
import ru.astrainteractive.astrarating.event.di.EventModule
import ru.astrainteractive.astrarating.feature.di.SharedModule
import ru.astrainteractive.astrarating.gui.di.GuiModule
import ru.astrainteractive.astrarating.integration.papi.di.PapiModule
import ru.astrainteractive.klibs.kdi.Module

interface RootModule : Module {

    // Modules
    val bukkitModule: BukkitModule

    val dbRatingModule: DBRatingModule

    val apiRatingModule: ApiRatingModule

    val papiModule: PapiModule?

    val sharedModule: SharedModule

    val guiModule: GuiModule

    val eventModule: EventModule

    val commandsModule: CommandsModule
}
