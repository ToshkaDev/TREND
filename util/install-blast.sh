#!/bin/bash

set -e

VERSION=$1
TARGET_DIR=$2

if [[ -z "$VERSION" || -z "$TARGET_DIR" ]]; then
	echo "Usage: $0 <Blast version> <target directory>"
	exit 1
fi

BIN_PATH=$TARGET_DIR/bin

if [[ -s "$BIN_PATH/segmasker" ]]; then
	echo "Blast version $VERSION is already installed"
	exit
fi

TEMP_DIR=$TARGET_DIR/tmp
mkdir -p $TEMP_DIR

TARBALL_BASENAME="ncbi-blast-${VERSION}+"
TARBALL_FILENAME="${TARBALL_BASENAME}-x64-linux.tar.gz"
BLAST_URL="https://ftp.ncbi.nlm.nih.gov/blast/executables/blast+/$VERSION/$TARBALL_FILENAME"
echo $BLAST_URLecho "Downloading Blast tarball"
cd $TEMP_DIR
curl -s "$BLAST_URL"

echo "Decompressing tarball"
tar zxvf $TARBALL_FILENAME
cp -r "$TEMP_DIR/$TARBALL_BASENAME/bin/" $BIN_PATH

echo "Cleaning up"
rm -r $TEMP_DIR

echo "Successfully installed Blast version $VERSION"