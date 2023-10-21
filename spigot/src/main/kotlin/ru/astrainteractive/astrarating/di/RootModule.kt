package ru.astrainteractive.astrarating.di

import ru.astrainteractive.astrarating.api.rating.di.ApiRatingModule
import ru.astrainteractive.astrarating.command.di.CommandsModule
import ru.astrainteractive.astrarating.db.rating.di.DBRatingModule
import ru.astrainteractive.astrarating.feature.allrating.data.AllRatingsRepository
import ru.astrainteractive.astrarating.gui.di.GuiModule
import ru.astrainteractive.astrarating.integration.papi.di.PapiModule
import ru.astrainteractive.klibs.kdi.Module
import ru.astrainteractive.klibs.kdi.Single

interface RootModule : Module {

    // Modules
    val servicesModule: ServicesModule

    val dbRatingModule: DBRatingModule
    val apiRatingModule: ApiRatingModule

    val commandsModule: CommandsModule
    val guiModule: GuiModule

    val papiModule: PapiModule?

    // Domain
    val allRatingsRepository: Single<AllRatingsRepository>
}
