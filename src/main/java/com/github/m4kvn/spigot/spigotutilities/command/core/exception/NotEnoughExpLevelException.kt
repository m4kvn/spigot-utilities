package com.github.m4kvn.spigot.spigotutilities.command.core.exception

import com.github.m4kvn.spigot.spigotutilities.command.core.CommandExecutorException

class NotEnoughExpLevelException(
    private val requireLevel: Int,
) : CommandExecutorException() {

    override val message: String
        get() = "Not enough Exp Level. (require: ${requireLevel})"
}