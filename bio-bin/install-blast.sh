#!/usr/bin/env bash

set -e

VERSION=$1
TARGET_DIR=$2
TEMP_DIR=$3

if [[ -z "$VERSION" || -z "$TARGET_DIR" ]]; then
	echo "Usage: $0 <Blast version> <target directory>"
	exit 1
fi

BIN_PATH=$TARGET_DIR/bin

if [[ -s "$BIN_PATH/segmasker" ]]; then
	echo "Blast version $VERSION is already installed"
	exit
fi

TARBALL_BASENAME="ncbi-blast-${VERSION}+-x64-linux"
TARBALL_FILENAME="$TARBALL_BASENAME.tar.gz"
BLAST_URL="ftp://ftp.ncbi.nlm.nih.gov/blast/executables/blast+/$VERSION/$TARBALL_FILENAME"

echo "Downloading Blast tarball"
cd $TEMP_DIR
wget -q BLAST_URL

echo "Decompressing tarball"
tar zxvf $TARBALL_FILENAME
cp "$TEMP_DIR/$TARBALL_BASENAME/bin/*" BIN_PATH

echo "Cleaning up"
rm -rf $TARBALL_BASENAME
rm $TARBALL_FILENAME

echo "Successfully installed Blast version $VERSION"