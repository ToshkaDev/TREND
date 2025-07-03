#!/bin/bash

set -e

VERSION=$1
MAFFT_MAIN_DIR=$2

if [[ -z "$VERSION" || -z "$MAFFT_MAIN_DIR" ]]; then
	echo "Usage: $0 <mafft version> <mafft main directory>"
	exit 1
fi

MAFFT_DIR=$MAFFT_MAIN_DIR/target_dir/mafft
BIN_PATH=$MAFFT_MAIN_DIR/target_dir/bin
TEMP_DIR=$MAFFT_MAIN_DIR/temp_dir

mkdir -p $MAFFT_MAIN_DIR
mkdir -p $MAFFT_DIR
mkdir -p $BIN_PATH
mkdir -p $TEMP_DIR

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
#rm -rf $TARBALL_BASENAME
#rm $TARBALL_FILENAME
#export PATH

echo "Successfully installed Mafft version $VERSION"