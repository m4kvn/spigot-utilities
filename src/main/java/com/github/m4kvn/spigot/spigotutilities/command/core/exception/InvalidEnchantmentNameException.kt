package com.github.m4kvn.spigot.spigotutilities.command.core.exception

import com.github.m4kvn.spigot.spigotutilities.command.core.CommandExecutorException

class InvalidEnchantmentNameException(
    private val enchantmentName: String,
) : CommandExecutorException() {

    override val message: String
        get() = "Invalid enchantment name. (${enchantmentName})"
}