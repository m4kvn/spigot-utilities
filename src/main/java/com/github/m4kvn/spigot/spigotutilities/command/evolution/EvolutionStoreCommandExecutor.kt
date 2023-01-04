package com.github.m4kvn.spigot.spigotutilities.command.evolution

import com.github.m4kvn.spigot.spigotutilities.Constants
import com.github.m4kvn.spigot.spigotutilities.command.core.SubCommandExecutor
import com.github.m4kvn.spigot.spigotutilities.command.core.exception.*
import com.github.m4kvn.spigot.spigotutilities.usecase.CreateEnchantedBookUsecase
import com.github.m4kvn.spigot.spigotutilities.usecase.FindEnchantmentUsecase
import org.bukkit.GameMode
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.koin.core.component.inject

class EvolutionStoreCommandExecutor : SubCommandExecutor() {
    private val findEnchantmentUsecase: FindEnchantmentUsecase by inject()
    private val createEnchantedBookUsecase: CreateEnchantedBookUsecase by inject()

    override val flags: List<String> = listOf("-s", "--store")
    override val usage: String = "<enchantment>"

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
            throw NoEnchantmentItemInMainHandException(enchantmentName)
        }

        val requireLevel = currentLevel * Constants.EVOLUTION_BASE_REQUIRE_LEVEL
        if (player.gameMode != GameMode.CREATIVE && requireLevel > player.level) {
            throw NotEnoughExpLevelException(requireLevel)
        }

        val inventoryEmptyPosition = player.inventory.firstEmpty()
        if (inventoryEmptyPosition == -1) {
            throw NotEnoughSpaceInInventoryException()
        }

        val enchantedBook = createEnchantedBookUsecase(enchantment, currentLevel)

        player.inventory.setItem(inventoryEmptyPosition, enchantedBook)
        mainHandItemStack.removeEnchantment(enchantment)
        if (player.gameMode != GameMode.CREATIVE) {
            player.level -= requireLevel
        }
    }
}