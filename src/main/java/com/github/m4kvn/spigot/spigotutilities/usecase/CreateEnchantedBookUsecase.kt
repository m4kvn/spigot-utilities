package com.github.m4kvn.spigot.spigotutilities.usecase

import com.github.m4kvn.spigot.spigotutilities.usecase.core.Usecase
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.EnchantmentStorageMeta

class CreateEnchantedBookUsecase : Usecase {

    operator fun invoke(enchantment: Enchantment, level: Int): ItemStack {
        val book = ItemStack(Material.ENCHANTED_BOOK, 1)
        val meta = book.itemMeta as EnchantmentStorageMeta
        meta.addStoredEnchant(enchantment, level, true)
        book.itemMeta = meta
        return book
    }
}