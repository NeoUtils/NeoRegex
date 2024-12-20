/*
 * NeoRegex.
 *
 * Copyright (C) 2024 Irineu A. Silva.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.neoutils.neoregex.core.common.util

import org.freedesktop.dbus.annotations.DBusInterfaceName
import org.freedesktop.dbus.connections.impl.DBusConnection
import org.freedesktop.dbus.connections.impl.DBusConnectionBuilder
import org.freedesktop.dbus.interfaces.DBusInterface
import org.freedesktop.dbus.types.UInt32
import org.freedesktop.dbus.types.Variant

class XDGDesktopPortal(
    private val connection: DBusConnection = DBusConnectionBuilder
        .forSessionBus()
        .build()
) : AutoCloseable {

    fun getTheme(): ColorTheme {

        val settings = connection.getRemoteObject(
            BUS,
            PATH,
            Settings::class.java
        )

        val theme = settings.Read(
            "org.freedesktop.appearance",
            "color-scheme"
        )

        return when (theme.value.value) {
            Theme.LIGHT.value -> ColorTheme.LIGHT_SYSTEM
            Theme.DARK.value -> ColorTheme.DARK_SYSTEM
            else -> ColorTheme.LIGHT_SYSTEM
        }
    }

    override fun close() {
        connection.close()
    }

    @DBusInterfaceName("org.freedesktop.portal.Settings")
    interface Settings : DBusInterface {
        fun Read(namespace: String, key: String): Variant<Variant<UInt32>>
    }

    enum class Theme(val value: UInt32) {
        DARK(UInt32(1)),
        LIGHT(UInt32(2)),
    }

    companion object {
        private const val BUS = "org.freedesktop.portal.Desktop"
        private const val PATH = "/org/freedesktop/portal/desktop"
    }
}