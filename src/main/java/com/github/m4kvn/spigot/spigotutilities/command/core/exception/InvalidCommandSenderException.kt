package com.github.m4kvn.spigot.spigotutilities.command.core.exception

import com.github.m4kvn.spigot.spigotutilities.command.core.CommandExecutorException
import org.bukkit.command.CommandSender
import kotlin.reflect.KClass

class InvalidCommandSenderException(
    private val validCommandSender: KClass<out CommandSender>,
) : CommandExecutorException() {

    override val message: String
        get() = "This is command can send from only ${validCommandSender.simpleName}."
}