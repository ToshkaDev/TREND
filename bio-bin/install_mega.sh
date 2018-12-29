#!/bin/bash

#10.0.5-1
VERSION=$1
TARGET_DIR=$2
TEMP_DIR=$3
MEGA_DIR=$TARGET_DIR/megacc
BIN_PATH=$TARGET_DIR/bin

if [[ -z "$VERSION" || -z "$TARGET_DIR" || -z "$TEMP_DIR" ]]; then
	echo "Usage: $0 <Mega version> <target directory> <temp directory>"
	exit 1
fi


if [[ -s "$BIN_PATH/megacc" ]]; then
	echo "Mega version $VERSION is already installed"
	exit
fi


DEB_FILENAME="megax-cc_${VERSION}_amd64.deb"
#will need to specify
MEGA_URL="path/$VERSION/$DEB_FILENAME"

echo "Downloading Mega deb file"
cd $TEMP_DIR
wget -q $MEGA_URL

echo "Installing Mega ..."
dpkg-deb -x "$TEMP_DIR/megax-cc_${VERSION}_amd64.deb" $MEGA_DIR/
ln -s $MEGA_DIR/usr/bin/megacc $BIN_PATH/

echo "Successfully installed Mega version $VERSION"