#!/bin/bash

set -e

# 11.0.13-1
VERSION=$1
TARGET_DIR=$2

MEGA_DIR=$TARGET_DIR/megacc
BIN_PATH=$TARGET_DIR/bin

if [[ -z "$VERSION" || -z "$TARGET_DIR" ]]; then
	echo "Usage: $0 <Mega version> <target directory>"
	exit 1
fi


if [[ -s "$BIN_PATH/megacc" ]]; then
	echo "Mega version $VERSION is already installed"
	exit
fi

TEMP_DIR=$TARGET_DIR/tmp
mkdir -p $TEMP_DIR
mkdir -p $MEGA_DIR
mkdir -p $BIN_PATH

# https://www.megasoftware.net/releases/mega-cc_11.0.13-1_amd64.deb
DEB_FILENAME="mega-cc_${VERSION}_amd64.deb"

MEGA_URL="https://www.megasoftware.net/releases/${DEB_FILENAME}"

echo "Downloading Mega deb file"
cd $TEMP_DIR
wget -q $MEGA_URL

echo "Installing Mega ..."
dpkg-deb -x "${DEB_FILENAME}" $MEGA_DIR/
ln -s $MEGA_DIR/usr/bin/megacc $BIN_PATH/

# Remove the temp direcotory together with the source file
rm -r $TEMP_DIR

echo "Successfully installed Mega version $VERSION"