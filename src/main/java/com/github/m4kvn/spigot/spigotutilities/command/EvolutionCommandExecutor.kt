package com.github.m4kvn.spigot.spigotutilities.command

import com.github.m4kvn.spigot.spigotutilities.Constants
import com.github.m4kvn.spigot.spigotutilities.command.core.exception.EmptyEnchantmentItemInMainHandException
import com.github.m4kvn.spigot.spigotutilities.command.core.exception.HasNotEnchantmentItemInMainHandException
import com.github.m4kvn.spigot.spigotutilities.command.core.exception.InvalidCommandSenderException
import com.github.m4kvn.spigot.spigotutilities.command.core.exception.NotEnoughExpLevelException
import com.github.m4kvn.spigot.spigotutilities.command.evolution.EvolutionDeleteCommandExecutor
import com.github.m4kvn.spigot.spigotutilities.command.evolution.EvolutionStoreCommandExecutor
import com.github.m4kvn.spigot.spigotutilities.send
import com.github.m4kvn.spigot.spigotutilities.usecase.FindEnchantmentUsecase
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.koin.core.component.inject

class EvolutionCommandExecutor : BaseCommandExecutor() {
    private val findEnchantmentUsecase: FindEnchantmentUsecase by inject()

    override val commandName: String = "evolution"

    override val subCommands = listOf(
        EvolutionStoreCommandExecutor(),
        EvolutionDeleteCommandExecutor(),
    )

    override fun onCommand(sender: CommandSender, args: List<String>) {
        val player = sender as? Player
            ?: throw InvalidCommandSenderException(Player::class)

        val mainHandItemStack = player.inventory.itemInMainHand
        if (mainHandItemStack.enchantments.isEmpty()) {
            throw EmptyEnchantmentItemInMainHandException()
        }

        if (args.isEmpty()) {
            val enchantments = mainHandItemStack.enchantments
            val message = buildString {
                appendLine("Available enchantment list of ${mainHandItemStack.type}")
                append(enchantments.keys.joinToString(separator = "\n") {
                    "- ${ChatColor.AQUA}${it.key.key}${ChatColor.RESET} (lv.${enchantments[it]})"
                })
            }
            sender.send { message }
            return
        }

        val enchantmentName = args.first()
        val enchantment = findEnchantmentUsecase(enchantmentName)
            ?: throw HasNotEnchantmentItemInMainHandException(enchantmentName)

        val currentLevel = mainHandItemStack.getEnchantmentLevel(enchantment)
        val requireLevel = currentLevel * Constants.EVOLUTION_BASE_REQUIRE_LEVEL
        if (player.gameMode != GameMode.CREATIVE && requireLevel > player.level) {
            throw NotEnoughExpLevelException(requireLevel)
        }

        mainHandItemStack.addUnsafeEnchantment(enchantment, currentLevel + 1)

        if (player.gameMode != GameMode.CREATIVE) {
            player.level -= requireLevel
        }
    }

    override fun getAllCompletions(sender: CommandSender, args: Array<out String>, index: Int?): List<String> {
        val player = sender as? Player ?: return emptyList()
        val enchantments = player.inventory.itemInMainHand.enchantments
        if (enchantments.isEmpty()) return emptyList()
        return enchantments.keys.map { it.key.key }
    }
}