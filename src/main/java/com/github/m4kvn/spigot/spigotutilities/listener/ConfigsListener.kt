package com.github.m4kvn.spigot.spigotutilities.listener

import com.github.m4kvn.spigot.spigotutilities.Configs
import com.github.m4kvn.spigot.spigotutilities.sendConsole
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent

class ConfigsListener : BaseListener() {

    @EventHandler
    fun onPlayerJoinEvent(event: PlayerJoinEvent) {
        if (plugin.config.contains(event.player.name)) return
        sendConsole { "NO configuration for ${event.player.name}" }
        sendConsole { "Creating default configurations..." }
        plugin.config["${event.player.name}.${Configs.DEATH_PENALTY.asPath}"] = true
        plugin.saveConfig()
    }

    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        if (plugin.config.getBoolean("${event.entity.name}.${Configs.DEATH_PENALTY.asPath}")) return
        event.keepInventory = true
        event.keepLevel = true
        event.drops.clear()
        event.droppedExp = 0
    }
}