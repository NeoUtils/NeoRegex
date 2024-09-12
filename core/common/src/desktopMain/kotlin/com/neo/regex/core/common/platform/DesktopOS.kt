package com.neo.regex.core.common.platform

enum class DesktopOS {
    WINDOWS,
    MAC_OS,
    LINUX;

    companion object {
        val Current: DesktopOS by lazy {
            val name = System.getProperty("os.name")
            when {
                name?.startsWith("Linux") == true -> LINUX
                name?.startsWith("Win") == true -> WINDOWS
                name?.startsWith("Mac") == true -> MAC_OS
                else -> error("Unsupported desktop platform: $name")
            }
        }
    }
}