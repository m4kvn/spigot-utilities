package com.github.m4kvn.spigot.spigotutilities.listener

import com.github.m4kvn.spigot.spigotutilities.Configs
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class BaseListener : Listener, KoinComponent {
    protected val plugin: JavaPlugin by inject()

    fun register() {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    protected fun Player.update(config: Configs, value: Any) {
        plugin.config[config.getFullPath(name)] = value
    }

    protected fun Player.has(config: Configs): Boolean {
        return plugin.config.contains(config.getFullPath(name))
    }

    protected fun Player.getBoolean(config: Configs): Boolean {
        return plugin.config.getBoolean(config.getFullPath(name))
    }
}