package com.github.m4kvn.spigot.spigotutilities.command

import com.github.m4kvn.spigot.spigotutilities.command.core.CommandExecutorException
import com.github.m4kvn.spigot.spigotutilities.command.evolution.EvolutionStoreCommandExecutor
import com.github.m4kvn.spigot.spigotutilities.send
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.NamespacedKey
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class EvolutionCommandExecutor : BaseCommandExecutor() {
    override val commandName: String = "evolution"

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.send { "This command must be executed by the player." }
            return false
        }
        val mainHandItem = sender.inventory.itemInMainHand
        val enchantments = mainHandItem.enchantments
        if (enchantments.isEmpty()) {
            sender.send { "This item has not any enchantments." }
            return false
        }
        if (args.isEmpty()) {
            sender.send {
                buildString {
                    appendLine("Available enchantment list of ${mainHandItem.type}")
                    append(enchantments.keys.joinToString(separator = "\n") {
                        "- ${ChatColor.AQUA}${it.key.key}${ChatColor.RESET} (lv.${enchantments[it]})"
                    })
                }
            }
            return true
        }
        return when (val flag = flags[args.first()]) {
            Flag.DELETE -> sender.executeDelete(mainHandItem, args.drop(1))
            Flag.STORE -> {
                val subCommand = EvolutionStoreCommandExecutor()
                try {
                    subCommand.onCommand(sender, args.drop(1))
                } catch (e: CommandExecutorException) {
                    sender.send { e.message }
                    if (e.showUsage) {
                        sender.send { "Usage: /$label $flag ${subCommand.usage}" }
                    }
                }
                true
            }

            else -> sender.executeEvo(mainHandItem, args.first())
        }
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>,
    ): MutableList<String>? {
        val player = sender as? Player ?: return null
        val enchantments = player.inventory.itemInMainHand.enchantments
        if (enchantments.isEmpty()) return null
        val enchantmentKeys = enchantments.keys.map { it.key.key }
        val allCompletions = flags.keys + enchantmentKeys
        val completions = when (args.size) {
            0 -> allCompletions
            1 -> {
                val arg = args[0]
                if (arg.isBlank())
                    allCompletions else
                    allCompletions.filter { it.matches("^${arg}.*".toRegex()) }
            }

            2 -> {
                if (flags.containsKey(args[0]))
                    enchantmentKeys.filter { it.matches("^${args[1]}.*".toRegex()) } else
                    emptyList()
            }

            else -> emptyList()
        }
        return completions.toMutableList()
    }

    private fun findEnchantment(name: String): Enchantment? {
        val enchantmentKey = NamespacedKey.fromString(name)
        return Enchantment.getByKey(enchantmentKey)
    }

    private fun Player.executeDelete(itemStack: ItemStack, args: List<String>): Boolean {
        if (args.isEmpty()) return sendInvalidArgsSizeMessage()
        val enchantmentName = args.first()
        val enchantment = findEnchantment(enchantmentName)
            ?: return sendInvalidEnchantmentNameMessage(enchantmentName)
        val currentLevel = itemStack.getEnchantmentLevel(enchantment)
        if (currentLevel == 1) {
            itemStack.removeEnchantment(enchantment)
        } else {
            itemStack.addUnsafeEnchantment(enchantment, currentLevel - 1)
        }
        level += REQUIRE_LEVEL
        return true
    }

    private fun Player.executeEvo(itemStack: ItemStack, enchantmentName: String): Boolean {
        val enchantment = findEnchantment(enchantmentName)
            ?: return sendInvalidEnchantmentNameMessage(enchantmentName)
        val currentLevel = itemStack.getEnchantmentLevel(enchantment)
        if (!isCreative) {
            if (level >= REQUIRE_LEVEL) {
                level -= REQUIRE_LEVEL
            } else {
                send { "Not enough Exp Level for evolution. (require: ${REQUIRE_LEVEL})" }
                return true
            }
        }
        itemStack.addUnsafeEnchantment(enchantment, currentLevel + 1)
        return true
    }

    private fun Player.sendInvalidArgsSizeMessage(): Boolean {
        send { "Invalid args size." }
        return false
    }

    private fun Player.sendInvalidEnchantmentNameMessage(name: String): Boolean {
        send { "Invalid enchantment name. (${name})" }
        return true
    }

    private val Player.isCreative: Boolean
        get() = gameMode == GameMode.CREATIVE

    companion object {
        private const val REQUIRE_LEVEL = 10

        private enum class Flag { DELETE, STORE }

        private val flags = mapOf(
            "-d" to Flag.DELETE,
            "-s" to Flag.STORE,
        )
    }
}