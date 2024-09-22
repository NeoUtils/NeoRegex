#!/bin/bash

# Install script for NeoRegex
# Usage: sudo ./install.sh

set -e

# Configuration variables
APP_NAME="NeoRegex"
APP_ID="com.neoutils.$APP_NAME"
INSTALL_DIR="/usr/local/share/$APP_NAME"
BIN_DIR="/usr/local/bin"
DESKTOP_FILE_DIR="/usr/share/applications"
ICON_SIZE="128x128"
ICON_DIR="/usr/share/icons/hicolor/$ICON_SIZE/apps"
METAINFO_DIR="/usr/share/metainfo"

# Ensure the script is run as root
if [[ $EUID -ne 0 ]]; then
   echo "This script must be run as root. Use sudo ./install.sh"
   exit 1
fi

echo "Installing $APP_NAME..."

# Create necessary directories
mkdir -p "$INSTALL_DIR"
mkdir -p "$ICON_DIR"
mkdir -p "$METAINFO_DIR"

# Copy application files
cp -r bin lib "$INSTALL_DIR/"

# Install the .desktop file
install -Dm644 "$APP_ID.desktop" "$DESKTOP_FILE_DIR/$APP_ID.desktop"

# Install the metainfo file
install -Dm644 "$APP_ID.metainfo.xml" "$METAINFO_DIR/$APP_ID.metainfo.xml"

# Install the icon
install -Dm644 "$INSTALL_DIR/lib/$APP_NAME.png" "$ICON_DIR/$APP_ID.png"

# Create a launcher script
cat << EOF > "$BIN_DIR/$APP_NAME"
#!/bin/bash
"$INSTALL_DIR/bin/$APP_NAME" "\$@"
EOF

# Make the launcher script executable
chmod +x "$BIN_DIR/$APP_NAME"

# Update desktop database
if command -v update-desktop-database >/dev/null 2>&1; then
    update-desktop-database "$DESKTOP_FILE_DIR"
fi

# Update icon cache
if command -v gtk-update-icon-cache >/dev/null 2>&1; then
    gtk-update-icon-cache -q "/usr/share/icons/hicolor"
fi

echo "Installation completed successfully!"
echo "You can run the application using the command: $APP_NAME or through the application menu."

