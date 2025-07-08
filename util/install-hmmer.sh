#!/bin/bash

set -e

VERSION=$1
TARGET_DIR=$2
TEMP_DIR=$TARGET_DIR/tmp

if [[ -z "$VERSION" || -z "$TARGET_DIR" ]]; then
	echo "Usage: $0 <hmmer3 version> <target directory>"
	exit 1
fi

BIN_PATH=$TARGET_DIR/bin

if [[ -s "$BIN_PATH/hmmscan" && -s "$BIN_PATH/hmmpress" ]]; then
	echo "HMMER3 version $VERSION is already installed"
	exit
fi

mkdir -p $TARGET_DIR
mkdir -p $TEMP_DIR
mkdir -p $BIN_PATH

TARBALL_BASENAME="hmmer-${VERSION}"
TARBALL_FILENAME="$TARBALL_BASENAME.tar.gz"
HMMER3_URL="http://eddylab.org/software/hmmer/$TARBALL_FILENAME"

echo "Downloading HMMER3 tarball ..."

cd $TEMP_DIR

#wget -q $HMMER3_URL
curl -sS "$HMMER3_URL"
echo "Decompressing tarball ..."
tar zxvf $TARBALL_FILENAME
cd $TARBALL_BASENAME
./configure --prefix $TARGET_DIR
make
make install
echo "Cleaning up"
rm -r $TEMP_DIR
PATH=$BIN_PATH:$PATH
export PATH

echo "Successfully installed HMMER3 version $VERSION"