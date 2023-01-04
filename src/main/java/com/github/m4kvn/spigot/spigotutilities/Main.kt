package com.github.m4kvn.spigot.spigotutilities

import com.github.m4kvn.spigot.spigotutilities.listener.FireProtectListener
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
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.server.TabCompleteEvent
import org.bukkit.inventory.ItemStack
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
        FireProtectListener().register()
        server.pluginManager.registerEvents(object : Listener {
            private val evoRegex = "^/(evolution|evo)(\\s*|\\s+.+)".toRegex()
            private val flags = listOf("-d")

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
            @EventHandler
            fun onTabCompleteEvent(event: TabCompleteEvent) {
                if (event.buffer.isBlank()) return
                if (!event.buffer.matches(evoRegex)) return
                val player = event.sender as? Player ?: return
                val bufferList = event.buffer.split("\\s+".toRegex()).drop(1)
                val enchantments = player.inventory.itemInMainHand.enchantments.keys.map { it.key.key }
                bufferList.forEachIndexed { index, buffer ->
                    event.completions = when (index) {
                        0 -> {
                            val allCompletions = flags + enchantments
                            if (buffer.isBlank())
                                allCompletions else
                                allCompletions.filter { it.matches("^${buffer}.*".toRegex()) }
                        }

                        else -> if (flags.contains(bufferList[index - 1]))
                            enchantments.filter { it.matches("^${buffer}.*".toRegex()) } else
                            emptyList()
                    }
                }
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
        private val flags = mapOf(
            "-d" to Flag.DELETE,
        )

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
            return when (flags[args.first()]) {
                Flag.DELETE -> sender.executeDelete(mainHandItem, args.drop(1))
                else -> sender.executeEvo(mainHandItem, args.first())
            }
        }

        private fun Player.executeDelete(itemStack: ItemStack, args: List<String>): Boolean {
            if (args.isEmpty()) {
                sendMessage("Invalid args size.")
                return false
            }
            val enchantmentName = args.first()
            val enchantment = findEnchantment(enchantmentName)
                ?: return sendInvalidEnchantmentNameMessage(enchantmentName)
            val currentLevel = itemStack.getEnchantmentLevel(enchantment)
            if (currentLevel == 1) {
                itemStack.removeEnchantment(enchantment)
            } else {
                itemStack.addUnsafeEnchantment(enchantment, currentLevel - 1)
            }
            level += REQUIRE_LEVEL
            return true
        }

        private fun Player.executeEvo(itemStack: ItemStack, enchantmentName: String): Boolean {
            val enchantment = findEnchantment(enchantmentName)
                ?: return sendInvalidEnchantmentNameMessage(enchantmentName)
            val currentLevel = itemStack.getEnchantmentLevel(enchantment)
            if (notEnoughExpLevel) {
                sendMessage("Not enough Exp Level for evolution. (require: ${REQUIRE_LEVEL})")
                return true
            }
            level -= REQUIRE_LEVEL
            itemStack.addUnsafeEnchantment(enchantment, currentLevel + 1)
            return true
        }

        private fun findEnchantment(name: String): Enchantment? {
            val enchantmentKey = NamespacedKey.fromString(name)
            return Enchantment.getByKey(enchantmentKey)
        }

        private fun Player.sendInvalidEnchantmentNameMessage(name: String): Boolean {
            sendMessage("Invalid enchantment name. (${name})")
            return true
        }

        enum class Flag {
            DELETE,
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