package ru.astrainteractive.astrarating.command.model

import ru.astrainteractive.astralibs.command.api.exception.CommandException

internal class UsageCommandException(msg: String = "Wrong argument usage") : CommandException(msg)
