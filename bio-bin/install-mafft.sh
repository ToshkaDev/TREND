#!/bin/bash

set -e

VERSION=$1
TARGET_DIR=$2
TEMP_DIR=$3

if [[ -z "$VERSION" || -z "$TARGET_DIR" ]]; then
	echo "Usage: $0 <mafft version> <target directory>"
	exit 1
fi

MAFFT_DIR=$TARGET_DIR/mafft
BIN_PATH=$TARGET_DIR/bin

if [[ -s "$BIN_PATH/mafft" ]]; then
	echo "Mafft version $VERSION is already installed"
	exit
fi

TARBALL_BASENAME="mafft-${VERSION}-without-extensions"
TARBALL_FILENAME="${TARBALL_BASENAME}-src.tgz"
MAFFT_URL="https://mafft.cbrc.jp/alignment/software/$TARBALL_FILENAME"

echo "Downloading Mafft ..."
cd $TEMP_DIR
wget -q $MAFFT_URL

echo "Decompressing tarball"
tar zxvf $TARBALL_FILENAME

echo "Installing Mafft ..."
cd "$TARBALL_BASENAME/core"
#change the first line in Makefile to point to installation directory in the system and the third line - to bin directory
sed -i -e "s|PREFIX = /usr/local|PREFIX = $MAFFT_DIR|" -e "s|BINDIR = \$(PREFIX)/bin|BINDIR = $BIN_PATH|" Makefile
make clean
make
make install

echo "Cleaning up"
cd $TEMP_DIR
rm -rf $TARBALL_BASENAME
rm $TARBALL_FILENAME
#export PATH

echo "Successfully installed Mafft version $VERSION"