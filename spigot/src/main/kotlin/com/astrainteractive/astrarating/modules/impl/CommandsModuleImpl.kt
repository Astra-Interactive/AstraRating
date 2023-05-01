package com.astrainteractive.astrarating.modules.impl

import com.astrainteractive.astrarating.commands.di.CommandsModule
import com.astrainteractive.astrarating.commands.rating.RatingCommandController
import com.astrainteractive.astrarating.domain.api.RatingDBApi
import com.astrainteractive.astrarating.domain.usecases.InsertUserUseCase
import com.astrainteractive.astrarating.gui.di.GuiModule
import com.astrainteractive.astrarating.gui.ratings.RatingsGUI
import com.astrainteractive.astrarating.modules.RootModule
import com.astrainteractive.astrarating.plugin.EmpireConfig
import com.astrainteractive.astrarating.plugin.PluginTranslation
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.Dependency
import ru.astrainteractive.astralibs.Factory
import ru.astrainteractive.astralibs.Single
import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.getValue

object CommandsModuleImpl : CommandsModule {
    private val rootModule: RootModule by RootModuleImpl
    private val guiModule: GuiModule by GuiModuleImpl

    override val dispatchers: Dependency<BukkitDispatchers> = rootModule.dispatchers
    override val scope: Dependency<AsyncComponent> = rootModule.scope
    override val translation: Dependency<PluginTranslation> = rootModule.translation
    override val config: Dependency<EmpireConfig> = rootModule.config
    override val dbApi: Dependency<RatingDBApi> = rootModule.dbApi
    override val insertUseCase: Dependency<InsertUserUseCase> = rootModule.insertUserUseCase
    override fun ratingsGUIFactory(player: Player): Factory<RatingsGUI> = Factory {
        RatingsGUI(player, guiModule)
    }

    override val ratingCommandController: Dependency<RatingCommandController> = Single {
        RatingCommandController(this)
    }
}
