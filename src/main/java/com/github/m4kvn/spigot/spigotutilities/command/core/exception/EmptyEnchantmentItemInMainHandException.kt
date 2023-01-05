package com.github.m4kvn.spigot.spigotutilities.command.core.exception

import com.github.m4kvn.spigot.spigotutilities.command.core.CommandExecutorException

class EmptyEnchantmentItemInMainHandException : CommandExecutorException() {

    override val message: String
        get() = "Enchantments in your main hand is empty."
}