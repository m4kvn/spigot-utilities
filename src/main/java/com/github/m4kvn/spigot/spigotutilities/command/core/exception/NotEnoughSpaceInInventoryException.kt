package com.github.m4kvn.spigot.spigotutilities.command.core.exception

import com.github.m4kvn.spigot.spigotutilities.command.core.CommandExecutorException

class NotEnoughSpaceInInventoryException : CommandExecutorException() {

    override val message: String
        get() = "Not enough space in your inventory."
}