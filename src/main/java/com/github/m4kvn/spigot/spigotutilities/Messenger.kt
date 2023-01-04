package com.github.m4kvn.spigot.spigotutilities

import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object Messenger : KoinComponent {
    private val plugin: JavaPlugin by inject()

    fun sendConsole(message: String) {
        plugin.server.consoleSender.sendMessage(message)
    }
}

fun sendConsole(block: () -> String) {
    Messenger.sendConsole(block())
}

fun CommandSender.send(block: () -> String) {
    sendMessage(block())
}