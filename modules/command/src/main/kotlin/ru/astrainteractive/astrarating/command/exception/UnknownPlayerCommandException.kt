package ru.astrainteractive.astrarating.command.exception

import ru.astrainteractive.astralibs.command.api.exception.CommandException

internal class UnknownPlayerCommandException(msg: String = "Player not found with that name") : CommandException(msg)
