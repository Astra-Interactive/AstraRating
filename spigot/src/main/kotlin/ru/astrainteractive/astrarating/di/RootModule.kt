package ru.astrainteractive.astrarating.di

import ru.astrainteractive.astrarating.api.rating.di.ApiRatingModule
import ru.astrainteractive.astrarating.command.di.CommandsModule
import ru.astrainteractive.astrarating.db.rating.di.DBRatingModule
import ru.astrainteractive.astrarating.feature.di.SharedModule
import ru.astrainteractive.astrarating.gui.di.GuiModule
import ru.astrainteractive.astrarating.integration.papi.di.PapiModule
import ru.astrainteractive.klibs.kdi.Module

interface RootModule : Module {

    // Modules
    val servicesModule: ServicesModule

    val dbRatingModule: DBRatingModule

    val apiRatingModule: ApiRatingModule

    val commandsModule: CommandsModule

    val guiModule: GuiModule

    val papiModule: PapiModule?

    val sharedModule: SharedModule
}
