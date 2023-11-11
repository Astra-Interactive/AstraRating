package ru.astrainteractive.astrarating.gui.di

import ru.astrainteractive.astrarating.di.RootModule
import ru.astrainteractive.astrarating.gui.router.GuiRouter
import ru.astrainteractive.astrarating.gui.router.GuiRouterImpl
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.getValue

interface GuiModule {
    val router: GuiRouter
    class Default(rootModule: RootModule) : GuiModule {
        override val router: GuiRouter by Provider {
            GuiRouterImpl(
                rootModule = rootModule
            )
        }
    }
}
