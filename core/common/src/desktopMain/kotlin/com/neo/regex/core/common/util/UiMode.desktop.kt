package com.neo.regex.core.common.util

import com.neo.regex.core.common.platform.DesktopOS
import com.neo.regex.core.common.platform.LinuxUI
import com.neo.regex.core.common.util.UiMode.DARK
import com.neo.regex.core.common.util.UiMode.LIGHT
import org.jetbrains.skiko.SystemTheme
import org.jetbrains.skiko.currentSystemTheme

fun UiMode.Companion.resolve(): UiMode {
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
