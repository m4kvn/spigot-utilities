package com.github.m4kvn.spigot.spigotutilities

enum class Configs {
    ENABLE_DEATH_PENALTY {
        override val values: List<String> = listOf("true", "false")
        override val default: Any = true
    },
    ENABLE_CHUNK_CHECKER {
        override val values: List<String> = listOf("true", "false")
        override val default: Any = false
    }
    ;

    fun getFullPath(playerName: String): String {
        return "${playerName}.${name.lowercase()}"
    }

    abstract val values: List<String>
    abstract val default: Any

    companion object {
        val pathList = values().map { it.name.lowercase() }

        fun find(path: String): Configs? {
            return runCatching {
                Configs.valueOf(path.uppercase())
            }.getOrNull()
        }
    }
}