package ru.astrainteractive.astrarating.command.exception

import ru.astrainteractive.astralibs.command.api.exception.CommandException

internal class UsageCommandException(msg: String = "Wrong argument usage") : CommandException(msg)
