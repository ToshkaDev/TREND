#!/bin/bash

set -e

VERSION=$1
TARGET_DIR=$2
BIN_PATH=$TARGET_DIR/bin
mkdir -p $TARGET_DIR
mkdir -p $BIN_PATH

if [[ -z "$VERSION" || -z "$TARGET_DIR" ]]; then
	echo "Usage: $0 <CD-HIT version> <target directory>"
	exit 1
fi

BASE_NAME=cd-hit-v${VERSION}-2019-0228
TARBALL=${BASE_NAME}.tar.gz
CDHIT_URL="https://github.com/weizhongli/cdhit/releases/download/V${VERSION}/${TARBALL}"

echo "Downloading CD_HIT ..."
wget -O $TARGET_DIR/$TARBALL $CDHIT_URL

echo "Decompressing tarball ..."
cd $TARGET_DIR
tar xvf $TARBALL

echo "Move executables to bin directory ..."
mv $BASE_NAME/* $BIN_PATH

echo "Clean up ..."
rm -r $BASE_NAME
rm $TARGET_DIR/$TARBALL

echo "Successfully installed CD-HIT version $VERSION"