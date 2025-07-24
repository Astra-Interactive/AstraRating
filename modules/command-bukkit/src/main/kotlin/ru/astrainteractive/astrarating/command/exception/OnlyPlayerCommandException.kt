package ru.astrainteractive.astrarating.command.exception

import ru.astrainteractive.astralibs.command.api.exception.CommandException

class OnlyPlayerCommandException : CommandException("This command is intented to use by players only")
