package com.github.m4kvn.spigot.spigotutilities.command.evolution

import com.github.m4kvn.spigot.spigotutilities.Constants
import com.github.m4kvn.spigot.spigotutilities.command.core.SubCommandExecutor
import com.github.m4kvn.spigot.spigotutilities.command.core.exception.HasNotEnchantmentItemInMainHandException
import com.github.m4kvn.spigot.spigotutilities.command.core.exception.InvalidArgsSizeException
import com.github.m4kvn.spigot.spigotutilities.command.core.exception.InvalidCommandSenderException
import com.github.m4kvn.spigot.spigotutilities.command.core.exception.InvalidEnchantmentNameException
import com.github.m4kvn.spigot.spigotutilities.usecase.FindEnchantmentUsecase
import org.bukkit.GameMode
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.koin.core.component.inject

class EvolutionDeleteCommandExecutor : SubCommandExecutor() {
    private val findEnchantmentUsecase: FindEnchantmentUsecase by inject()

    override val flags: List<String> = listOf("-d", "--delete")
    override val usage: String = "<evolution>"

    override fun onCommand(sender: CommandSender, args: List<String>) {
        val player = sender as? Player
            ?: throw InvalidCommandSenderException(Player::class)
        if (args.isEmpty()) {
            throw InvalidArgsSizeException()
        }

        val enchantmentName = args.first()
        val enchantment = findEnchantmentUsecase(enchantmentName)
            ?: throw InvalidEnchantmentNameException(enchantmentName)

        val mainHandItemStack = player.inventory.itemInMainHand
        val currentLevel = mainHandItemStack.getEnchantmentLevel(enchantment)
        if (currentLevel == 0) {
            throw HasNotEnchantmentItemInMainHandException(enchantmentName)
        }

        if (currentLevel == 1) {
            mainHandItemStack.removeEnchantment(enchantment)
        } else {
            mainHandItemStack.addUnsafeEnchantment(enchantment, currentLevel - 1)
        }

        if (player.gameMode != GameMode.CREATIVE) {
            player.level += currentLevel * Constants.EVOLUTION_BASE_REQUIRE_LEVEL
        }
    }
}