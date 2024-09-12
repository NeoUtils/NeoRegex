package com.neo.regex.core.common.util

import com.neo.regex.core.common.platform.DesktopOS
import com.neo.regex.core.common.platform.LinuxUI
import org.jetbrains.skiko.SystemTheme
import org.jetbrains.skiko.currentSystemTheme

enum class UiMode {
    LIGHT,
    DARK;

    companion object {
        fun resolve(): UiMode {
            return when (DesktopOS.Current) {
                DesktopOS.WINDOWS,
                DesktopOS.MAC_OS -> when (currentSystemTheme) {
                    SystemTheme.LIGHT -> LIGHT
                    SystemTheme.DARK -> DARK
                    SystemTheme.UNKNOWN -> LIGHT
                }

                DesktopOS.LINUX -> when (LinuxUI.Current) {
                    LinuxUI.GNOME -> GnomeSettings.getUiMode()
                    LinuxUI.OTHER -> LIGHT
                }
            }
        }
    }
}

val UiMode.isDark: Boolean
    get() = this == UiMode.DARK
