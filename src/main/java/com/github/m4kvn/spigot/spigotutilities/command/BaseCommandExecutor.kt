package com.github.m4kvn.spigot.spigotutilities.command

import com.github.m4kvn.spigot.spigotutilities.command.core.CommandExecutorException
import com.github.m4kvn.spigot.spigotutilities.command.core.SubCommandExecutor
import com.github.m4kvn.spigot.spigotutilities.send
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class BaseCommandExecutor : CommandExecutor, TabCompleter, KoinComponent {
    protected val plugin: JavaPlugin by inject()

    abstract val commandName: String
    abstract val subCommands: List<SubCommandExecutor>

    private val flags: List<String>
        get() = subCommands.flatMap { it.flags }

    @Throws(CommandExecutorException::class)
    abstract fun onCommand(sender: CommandSender, args: List<String>)

    abstract fun getAllCompletions(
        sender: CommandSender,
        args: Array<out String>,
        index: Int? = null,
    ): List<String>

    fun register() {
        val command = plugin.getCommand(commandName) ?: return
        command.setExecutor(this)
        command.tabCompleter = this
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val flag = args.firstOrNull()
        val subCommand = subCommands.find { it.flags.contains(flag) }

        try {
            subCommand
                ?.onCommand(sender, args.drop(1))
                ?: onCommand(sender, args.toList())
        } catch (e: CommandExecutorException) {
            sender.send { "${ChatColor.RED}${e.message}${ChatColor.RESET}" }

            if (e.showUsage) {
                val message = if (subCommand != null)
                    "/$label $flag ${subCommand.usage}" else
                    command.usage.replace("<command>", label)
                sender.send { "Usage: ${ChatColor.YELLOW}${message}${ChatColor.RESET}" }
            }
        }

        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>,
    ): MutableList<String>? {
        val completions = when (args.size) {
            0 -> flags + getAllCompletions(sender, args)
            1 -> getCompletionsWithFlags(sender, args)
            2 -> getCompletions(sender, args)
            else -> emptyList()
        }
        return completions.toMutableList()
    }

    private fun getCompletionsWithFlags(
        sender: CommandSender,
        args: Array<out String>,
    ): List<String> {
        val allCompletions = flags + getAllCompletions(sender, args, index = 0)
        return if (args[0].isBlank())
            allCompletions else
            allCompletions.filter { it.matches("^${args[0]}.*".toRegex()) }
    }

    private fun getCompletions(
        sender: CommandSender,
        args: Array<out String>,
    ): List<String> {
        val completions = getAllCompletions(sender, args, index = 1)
        return if (flags.contains(args[0]))
            completions.filter { it.matches("^${args[1]}.*".toRegex()) } else
            emptyList()
    }
}