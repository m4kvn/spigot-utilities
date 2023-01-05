package com.github.m4kvn.spigot.spigotutilities.command

import com.github.m4kvn.spigot.spigotutilities.Configs
import com.github.m4kvn.spigot.spigotutilities.command.core.SubCommandExecutor
import com.github.m4kvn.spigot.spigotutilities.command.core.exception.InvalidArgsSizeException
import com.github.m4kvn.spigot.spigotutilities.command.core.exception.InvalidArgumentException
import com.github.m4kvn.spigot.spigotutilities.command.core.exception.InvalidCommandSenderException
import com.github.m4kvn.spigot.spigotutilities.send
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.spigotmc.SpigotConfig.config

class UtilitiesCommandExecutor : BaseCommandExecutor() {

    override val commandName: String = "utilities"

    override val subCommands: List<SubCommandExecutor> = listOf()

    override fun onCommand(sender: CommandSender, args: List<String>) {
        if (sender !is Player) throw InvalidCommandSenderException(Player::class)
        if (args.size < 2) throw InvalidArgsSizeException()

        val parameter = Configs.find(args[0]) ?: throw InvalidArgumentException(args[0])
        val value = args[1].toBooleanStrictOrNull() ?: throw InvalidArgumentException(args[1])

        config["${sender.name}.${parameter.asPath}"] = value
        plugin.saveConfig()
        sender.send { "Complete configuration changes." }
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

    override fun getAllCompletions(sender: CommandSender, args: Array<out String>, index: Int?): List<String> {
        if (sender !is Player) return emptyList()
        return when (index) {
            null -> Configs.pathList
            0 -> Configs.pathList
            1 -> Configs.find(args[0])?.values ?: emptyList()
            else -> emptyList()
        }
    }
}