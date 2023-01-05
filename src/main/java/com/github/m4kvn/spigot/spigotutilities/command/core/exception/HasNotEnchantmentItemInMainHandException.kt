package com.github.m4kvn.spigot.spigotutilities.command.core.exception

import com.github.m4kvn.spigot.spigotutilities.command.core.CommandExecutorException

class HasNotEnchantmentItemInMainHandException(
    private val enchantmentName: String,
) : CommandExecutorException() {

    override val message: String
        get() = "The item in your main hand has not enchantment named $enchantmentName."
}