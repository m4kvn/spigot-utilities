package com.github.m4kvn.spigot.spigotutilities

import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.NamespacedKey
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBurnEvent
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
        setCommandExecutor()
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
            fun onBlockBurn(event: BlockBurnEvent) {
                event.isCancelled = true
            }
            @EventHandler
            fun onEntityExplode(event: EntityExplodeEvent) {
                event.blockList().clear()
            }
            @EventHandler
            fun onPlayerJoinEvent(event: PlayerJoinEvent) {
                if (config.contains(event.player.name)) return
                server.consoleSender.sendMessage("NO configuration for ${event.player.name}")
                server.consoleSender.sendMessage("Creating default configurations...")
                config["${event.player.name}.${Configs.DEATH_PENALTY.asPath}"] = true
                saveConfig()
            }
        }, this)
    }

    private fun setCommandExecutor() {
        getCommand("utilities")?.setExecutor { sender, _, _, args ->
            if (sender !is Player) {
                sender.sendMessage("This command must be executed by the player.")
                return@setExecutor false
            }
            if (args.size < 2) {
                sender.sendMessage("Invalid arguments size.")
                return@setExecutor false
            }
            val parameter = Configs.find(args[0])
            if (parameter == null) {
                sender.sendMessage("Invalid configuration name.")
                return@setExecutor false
            }
            val value = args[1].toBooleanStrictOrNull()
            if (value == null) {
                sender.sendMessage("Invalid configuration value.")
                return@setExecutor false
            }
            config["${sender.name}.${parameter.asPath}"] = value
            saveConfig()
            sender.sendMessage("Complete configuration changes.")
            true
        }
        getCommand("evolution")?.setExecutor(EvolutionCommandExecutor())
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }

    class EvolutionCommandExecutor : CommandExecutor {

        private val Player.notEnoughExpLevel: Boolean
            get() {
                if (gameMode == GameMode.CREATIVE) return false
                if (level >= REQUIRE_LEVEL) return false
                return true
            }

        override fun onCommand(
            sender: CommandSender,
            command: Command,
            label: String,
            args: Array<out String>,
        ): Boolean {
            if (sender !is Player) {
                sender.sendMessage("This command must be executed by the player.")
                return false
            }
            val mainHandItem = sender.inventory.itemInMainHand
            val enchantments = mainHandItem.enchantments
            if (enchantments.isEmpty()) {
                sender.sendMessage("This item has not any enchantments.")
                return false
            }
            if (args.isEmpty()) {
                sender.sendMessage(buildString {
                    appendLine("Available enchantment list of ${mainHandItem.type}")
                    append(enchantments.keys.joinToString(separator = "\n") {
                        "- ${ChatColor.AQUA}${it.key.key}${ChatColor.RESET} (lv.${enchantments[it]})"
                    })
                })
                return true
            }
            if (args.size > 1) {
                sender.sendMessage("Invalid args size.")
                return false
            }
            val enchantmentName = NamespacedKey.fromString(args[0])
            val enchantment = Enchantment.getByKey(enchantmentName)
            if (enchantment == null) {
                sender.sendMessage("Invalid enchantment name. (${args[0]})")
                return true
            }
            val currentLevel = mainHandItem.getEnchantmentLevel(enchantment)
            if (sender.notEnoughExpLevel) {
                sender.sendMessage("Not enough Exp Level for evolution. (require: ${REQUIRE_LEVEL})")
                return true
            }
            sender.level = sender.level - REQUIRE_LEVEL
            mainHandItem.addUnsafeEnchantment(enchantment, currentLevel + 1)
            return true
        }

        companion object {
            private const val REQUIRE_LEVEL = 10
        }
    }

    companion object {
        enum class Configs {
            DEATH_PENALTY,
            ;

            val asPath: String
                get() = name.lowercase()

            companion object {
                fun find(path: String): Configs? {
                    return runCatching {
                        Configs.valueOf(path.uppercase())
                    }.getOrNull()
                }
            }
        }
    }
}