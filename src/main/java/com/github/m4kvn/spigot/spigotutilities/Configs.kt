package com.github.m4kvn.spigot.spigotutilities

enum class Configs {
    DEATH_PENALTY {
        override val values: List<String> = listOf("true", "false")
    },
    ;

    val asPath: String
        get() = name.lowercase()

    abstract val values: List<String>

    companion object {
        val pathList = values().map { it.asPath }

        fun find(path: String): Configs? {
            return runCatching {
                Configs.valueOf(path.uppercase())
            }.getOrNull()
        }
    }
}