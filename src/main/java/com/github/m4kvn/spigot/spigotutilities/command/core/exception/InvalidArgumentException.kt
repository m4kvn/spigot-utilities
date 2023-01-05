package com.github.m4kvn.spigot.spigotutilities.command.core.exception

import com.github.m4kvn.spigot.spigotutilities.command.core.CommandExecutorException

class InvalidArgumentException(
    private val invalidArg: String,
) : CommandExecutorException(showUsage = true) {

    override val message: String
        get() = "Invalid argument ($invalidArg)"
}