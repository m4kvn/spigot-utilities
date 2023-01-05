package com.github.m4kvn.spigot.spigotutilities.command.core

import org.bukkit.command.CommandSender
import org.koin.core.component.KoinComponent

abstract class SubCommandExecutor : KoinComponent {
    abstract val flags: List<String>
    abstract val usage: String

    @Throws(CommandExecutorException::class)
    abstract fun onCommand(sender: CommandSender, args: List<String>)
}