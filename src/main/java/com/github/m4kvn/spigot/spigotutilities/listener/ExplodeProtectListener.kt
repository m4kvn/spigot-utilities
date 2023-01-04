package com.github.m4kvn.spigot.spigotutilities.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityExplodeEvent

class ExplodeProtectListener : BaseListener() {

    @EventHandler
    fun onEntityExplode(event: EntityExplodeEvent) {
        event.blockList().clear()
    }
}