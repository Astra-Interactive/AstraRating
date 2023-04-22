package com.astrainteractive.astrarating.exception

import org.bukkit.command.CommandSender

sealed class ValidationException : Exception() {
    class NoPermission(val sender: CommandSender) : ValidationException()
    class DiscordNotLinked(val sender: CommandSender) : ValidationException()
    class PlayerNotExists(val sender: CommandSender) : ValidationException()
    class SamePlayer(val sender: CommandSender) : ValidationException()
    class NotEnoughOnServer(val sender: CommandSender) : ValidationException()
    class NotEnoughOnDiscord(val sender: CommandSender) : ValidationException()
    class OnlyPlayerCommand(val sender: CommandSender) : ValidationException()
    class DBException(val sender: CommandSender) : ValidationException()
    class WrongMessageLength(val sender: CommandSender) : ValidationException()
    class AlreadyMaxVotesOnPlayer(val sender: CommandSender) : ValidationException()
    class AlreadyMaxDayVotes(val sender: CommandSender) : ValidationException()
}