package com.astrainteractive.astrarating.commands.di

import com.astrainteractive.astrarating.commands.rating.di.RatingCommandControllerModule
import com.astrainteractive.astrarating.commands.rating.di.RatingCommandModule
import ru.astrainteractive.astralibs.Module

interface CommandsModule : Module, RatingCommandModule, RatingCommandControllerModule
