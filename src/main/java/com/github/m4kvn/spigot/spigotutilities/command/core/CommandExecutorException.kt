package com.github.m4kvn.spigot.spigotutilities.command.core

abstract class CommandExecutorException(
    val showUsage: Boolean = false,
) : Exception() {
    abstract override val message: String
}