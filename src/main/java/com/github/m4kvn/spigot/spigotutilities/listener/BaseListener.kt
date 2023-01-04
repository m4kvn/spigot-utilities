package com.github.m4kvn.spigot.spigotutilities.listener

import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class BaseListener : Listener, KoinComponent {
    private val plugin: JavaPlugin by inject()

    fun register() {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }
}