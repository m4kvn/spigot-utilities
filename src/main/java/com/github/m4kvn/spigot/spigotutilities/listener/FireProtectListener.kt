package com.github.m4kvn.spigot.spigotutilities.listener

import org.bukkit.Material
import org.bukkit.block.BlockState
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBurnEvent
import org.bukkit.event.block.BlockSpreadEvent

class FireProtectListener : BaseListener() {

    @EventHandler
    fun onBlockBurn(event: BlockBurnEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun onBlockSpread(event: BlockSpreadEvent) {
        event.isCancelled = event.newState.isFire
    }

    private val BlockState.isFire: Boolean
        get() = blockData.material == Material.FIRE
}