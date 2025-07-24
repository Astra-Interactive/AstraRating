package ru.astrainteractive.astrarating.command.exception

import ru.astrainteractive.astralibs.command.api.exception.CommandException

class UnknownPlayerCommandException(msg: String = "Player not found with that name") : CommandException(msg)
