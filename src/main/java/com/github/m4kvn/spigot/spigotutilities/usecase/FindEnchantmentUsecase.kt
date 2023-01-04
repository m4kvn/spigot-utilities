package com.github.m4kvn.spigot.spigotutilities.usecase

import com.github.m4kvn.spigot.spigotutilities.usecase.core.Usecase
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment

class FindEnchantmentUsecase : Usecase {

    operator fun invoke(enchantmentName: String): Enchantment? {
        val enchantmentKey = NamespacedKey.fromString(enchantmentName)
        return Enchantment.getByKey(enchantmentKey)
    }
}