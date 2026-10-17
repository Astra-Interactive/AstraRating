package ru.astrainteractive.astrarating.gui.api.di

import ru.astrainteractive.astrarating.gui.api.router.api.GuiRouter

interface GuiModule {
    val router: GuiRouter
}
