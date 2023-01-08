package com.github.m4kvn.spigot.spigotutilities.listener

import com.github.m4kvn.spigot.spigotutilities.Configs
import com.github.m4kvn.spigot.spigotutilities.send
import org.bukkit.ChatColor
import org.bukkit.Chunk
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerMoveEvent

class ChunkCheckListener : BaseListener() {

    @EventHandler
    fun onPlayerMoved(event: PlayerMoveEvent) {
        if (!event.player.getBoolean(Configs.ENABLE_CHUNK_CHECKER)) return
        val fromChunk = event.from.chunk
        val toChunk = event.to?.chunk ?: return
        if (toChunk.isSame(fromChunk)) return
        event.player.send {
            buildString {
                append("Move chunk: ")
                append("{${fromChunk.x},${fromChunk.z}}")
                append(" -> ")
                append("${ChatColor.AQUA}{${toChunk.x},${toChunk.z}}${ChatColor.RESET}")
            }
        }
        if (toChunk.isSlimeChunk) {
            event.player.send {
                "${ChatColor.GREEN}This chunk is slime chunk.${ChatColor.RESET}"
            }
        }
    }

    private fun Chunk.isSame(chunk: Chunk): Boolean {
        return world.uid == chunk.world.uid
                && x == chunk.x
                && z == chunk.z
    }
}