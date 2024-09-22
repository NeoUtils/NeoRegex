#!/bin/bash

# Uninstall script for NeoRegex
# Usage: sudo ./uninstall.sh

set -e

APP_NAME="NeoRegex"
APP_ID="com.neoutils.NeoRegex"
INSTALL_DIR="/usr/local/share/$APP_NAME"
BIN_FILE="/usr/local/bin/$APP_NAME"
DESKTOP_FILE="/usr/share/applications/$APP_ID.desktop"
ICON_SIZE="128x128"
ICON_FILE="/usr/share/icons/hicolor/$ICON_SIZE/apps/$APP_ID.png"
METAINFO_FILE="/usr/share/metainfo/$APP_ID.metainfo.xml"

# Ensure the script is run as root
if [[ $EUID -ne 0 ]]; then
   echo "This script must be run as root. Use sudo ./uninstall.sh"
   exit 1
fi

echo "Uninstalling $APP_NAME..."

# Remove application files
rm -rf "$INSTALL_DIR"

# Remove the launcher script
rm -f "$BIN_FILE"

# Remove the .desktop file
rm -f "$DESKTOP_FILE"

# Remove the icon
rm -f "$ICON_FILE"

# Remove the metainfo file
rm -f "$METAINFO_FILE"

# Update desktop database
if command -v update-desktop-database >/dev/null 2>&1; then
    update-desktop-database
fi

# Update icon cache
if command -v gtk-update-icon-cache >/dev/null 2>&1; then
    gtk-update-icon-cache -q "/usr/share/icons/hicolor"
fi

echo "Uninstallation completed successfully!"

