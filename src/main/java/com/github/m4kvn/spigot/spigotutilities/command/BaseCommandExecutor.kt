package com.github.m4kvn.spigot.spigotutilities.command

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class BaseCommandExecutor : CommandExecutor, TabCompleter, KoinComponent {
    private val plugin: JavaPlugin by inject()

    abstract val commandName: String

    fun register() {
        val command = plugin.getCommand(commandName) ?: return
        command.setExecutor(this)
        command.tabCompleter = this
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>,
    ): MutableList<String>? = mutableListOf()
}