package com.github.m4kvn.spigot.spigotutilities

import com.github.m4kvn.spigot.spigotutilities.command.EvolutionCommandExecutor
import com.github.m4kvn.spigot.spigotutilities.command.UtilitiesCommandExecutor
import com.github.m4kvn.spigot.spigotutilities.listener.ConfigsListener
import com.github.m4kvn.spigot.spigotutilities.listener.ExplodeProtectListener
import com.github.m4kvn.spigot.spigotutilities.listener.FireProtectListener
import com.github.m4kvn.spigot.spigotutilities.usecase.CreateEnchantedBookUsecase
import com.github.m4kvn.spigot.spigotutilities.usecase.FindEnchantmentUsecase
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.context.startKoin
import org.koin.dsl.module

@Suppress("unused")
class Main : JavaPlugin() {
    private val module = module {
        single<JavaPlugin> { this@Main }
        factory { FindEnchantmentUsecase() }
        factory { CreateEnchantedBookUsecase() }
    }

    override fun onEnable() {
        startKoin { modules(module) }
        UtilitiesCommandExecutor().register()
        EvolutionCommandExecutor().register()
        ConfigsListener().register()
        FireProtectListener().register()
        ExplodeProtectListener().register()
    }
}