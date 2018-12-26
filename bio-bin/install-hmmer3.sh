#!/bin/bash

set -e

VERSION=$1
TARGET_DIR=$2

if [[ -z "$VERSION" || -z "$TARGET_DIR" ]]; then
	echo "Usage: $0 <hmmer3 version> <target directory>"
	exit 1
fi

BIN_PATH=$TARGET_DIR/bin

if [[ -s "$BIN_PATH/hmmscan" &&
	-s "$BIN_PATH/hmmpress" ]]; then
	echo "HMMER3 version $VERSION is already installed"
	exit
fi

TARBALL_BASENAME="hmmer-${VERSION}-linux-intel-x86_64"
TARBALL_FILENAME="$TARBALL_BASENAME.tar.gz"
HMMER3_URL="http://eddylab.org/software/hmmer3/$VERSION/$TARBALL_FILENAME"

echo "Downloading HMMER3 tarball"
cd /tmp
rm -f $TARBALL_FILENAME
wget -q $HMMER3_URL
echo "Decompressing tarball"
tar zxvf $TARBALL_FILENAME
cd $TARBALL_BASENAME
./configure --prefix $TARGET_DIR
make
make install
echo "Cleaning up"
cd /tmp
rm -rf $TARBALL_BASENAME
rm $TARBALL_FILENAME
PATH=$BIN_PATH:$PATH
export PATH

echo "Successfully installed HMMER3 version $VERSION"
