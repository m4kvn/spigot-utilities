package com.github.m4kvn.spigot.spigotutilities.listener

import com.github.m4kvn.spigot.spigotutilities.Configs
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent

class ConfigsListener : BaseListener() {

    @EventHandler
    fun onPlayerJoinEvent(event: PlayerJoinEvent) {
        var isUpdated = false
        Configs.values()
            .filterNot { event.player.has(it) }
            .forEach {
                event.player.update(it, it.default)
                isUpdated = true
            }
        if (isUpdated) {
            plugin.saveConfig()
        }
    }

    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        if (event.entity.getBoolean(Configs.ENABLE_DEATH_PENALTY)) return
        event.keepInventory = true
        event.keepLevel = true
        event.drops.clear()
        event.droppedExp = 0
    }
}