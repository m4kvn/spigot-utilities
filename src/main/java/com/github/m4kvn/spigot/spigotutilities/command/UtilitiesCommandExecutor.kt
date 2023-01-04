package com.github.m4kvn.spigot.spigotutilities.command

import com.github.m4kvn.spigot.spigotutilities.Configs
import com.github.m4kvn.spigot.spigotutilities.send
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.spigotmc.SpigotConfig.config

class UtilitiesCommandExecutor : BaseCommandExecutor() {
    override val commandName: String = "utilities"

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.send { "This command must be executed by the player." }
            return false
        }
        if (args.size < 2) {
            sender.send { "Invalid arguments size." }
            return false
        }
        val parameter = Configs.find(args[0])
        if (parameter == null) {
            sender.send { "Invalid configuration name." }
            return false
        }
        val value = args[1].toBooleanStrictOrNull()
        if (value == null) {
            sender.send { "Invalid configuration value." }
            return false
        }
        config["${sender.name}.${parameter.asPath}"] = value
        plugin.saveConfig()
        sender.send { "Complete configuration changes." }
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>,
    ): MutableList<String>? {
        if (sender !is Player) return null
        val completions = when (args.size) {
            0 -> Configs.pathList
            1 -> if (args[0].isBlank())
                Configs.pathList else
                Configs.pathList.filter { it.matches("^${args[0]}.*".toRegex()) }

            2 -> Configs.find(args[0])?.values
                ?.filter { it.matches("^${args[1]}.*".toRegex()) }
                ?: emptyList()

            else -> emptyList()
        }
        return completions.toMutableList()
    }
}