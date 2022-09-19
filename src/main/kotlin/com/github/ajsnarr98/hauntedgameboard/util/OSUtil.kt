package com.github.ajsnarr98.hauntedgameboard.util

object OSUtil {

    val targetSharedLibExt: String by lazy {
        when (currentOS) {
            OS.Windows -> "dll"
            OS.Linux -> "so"
            OS.MacOS -> "dylib"
        }
    }

    /**
     * Postfix for shared libs pre-compiled and in git.
     */
    val targetSharedLibPostfix: String by lazy {
        currentTarget.id
    }

    val currentArch by lazy {
        val osArch = System.getProperty("os.arch")
        when (osArch) {
            "x86_64", "amd64" -> Arch.X64
            "aarch64" -> Arch.Arm64
            else -> error("Unsupported OS arch: $osArch")
        }
    }

    val currentOS: OS by lazy {
        val os = System.getProperty("os.name")
        when {
            os.equals("Mac OS X", ignoreCase = true) -> OS.MacOS
            os.startsWith("Win", ignoreCase = true) -> OS.Windows
            os.startsWith("Linux", ignoreCase = true) -> OS.Linux
            else -> error("Unknown OS name: $os")
        }
    }

    val currentTarget: Target by lazy {
        Target(currentOS, currentArch)
    }

    val SUPPORTED_TARGETS = listOf(
        Target(OS.Linux, Arch.X64),
        Target(OS.Linux, Arch.Arm64),
        Target(OS.Windows, Arch.X64),
        Target(OS.MacOS, Arch.X64),
        Target(OS.MacOS, Arch.Arm64),
    )

    data class Target(val os: OS, val arch: Arch) {
        val id: String
            get() = "${os.id}-${arch.id}"
    }

    enum class OS(val id: String) {
        Linux("linux"),
        Windows("windows"),
        MacOS("macos")
    }

    enum class Arch(val id: String) {
        X64("x64"),
        Arm64("arm64")
    }
}