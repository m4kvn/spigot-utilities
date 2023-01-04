package com.github.m4kvn.spigot.spigotutilities

import com.github.m4kvn.spigot.spigotutilities.command.EvolutionCommandExecutor
import com.github.m4kvn.spigot.spigotutilities.command.UtilitiesCommandExecutor
import com.github.m4kvn.spigot.spigotutilities.listener.FireProtectListener
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.context.startKoin
import org.koin.dsl.module

@Suppress("unused")
class Main : JavaPlugin() {
    private val module = module {
        single<JavaPlugin> { this@Main }
    }

    override fun onEnable() {
        startKoin { modules(module) }
        UtilitiesCommandExecutor().register()
        EvolutionCommandExecutor().register()
        FireProtectListener().register()
        registerEvents()
    }

    private fun registerEvents() {
        server.pluginManager.registerEvents(object : Listener {
            @EventHandler
            fun onPlayerDeath(event: PlayerDeathEvent) {
                if (config.getBoolean("${event.entity.name}.${Configs.DEATH_PENALTY.asPath}")) return
                event.keepInventory = true
                event.keepLevel = true
                event.drops.clear()
                event.droppedExp = 0
            }
            @EventHandler
            fun onEntityExplode(event: EntityExplodeEvent) {
                event.blockList().clear()
            }
            @EventHandler
            fun onPlayerJoinEvent(event: PlayerJoinEvent) {
                if (config.contains(event.player.name)) return
                sendConsole { "NO configuration for ${event.player.name}" }
                sendConsole { "Creating default configurations..." }
                config["${event.player.name}.${Configs.DEATH_PENALTY.asPath}"] = true
                saveConfig()
            }
        }, this)
    }
}