#!/bin/bash

set -e

VERSION=$1
TARGET_DIR=$2
BIN_PATH=$TARGET_DIR/bin
LIB_PATH=$TARGET_DIR/lib

mkdir -p $BIN_PATH 
mkdir -p $LIB_PATH

if [[ -z "$TARGET_DIR" ]]; then
	echo "Usage: $0 <target directory>"
	exit 1
fi

if [[ -s "$BIN_PATH/decodeanhmm.Linux_x86_64" ]]; then
	echo "TMHMM2 is already installed"
	exit
fi

FILEID=1KtVL9IjD5GkjRPGvEeI3z_kWwZnlBOEb
BASE_NAME=tmhmm-2.0c
TARBALL=${BASE_NAME}.tar.gz
curl -L -o "$TARGET_DIR/$TARBALL" "https://docs.google.com/uc?export=download&id=${FILEID}"

echo "Decompressing tarball ..."
cd $TARGET_DIR
tar xvf $TARBALL

mv $TARGET_DIR/$BASE_NAME/bin/* $BIN_PATH
mv $TARGET_DIR/$BASE_NAME/lib/* $LIB_PATH

sed -i '1 s|/usr/local/bin/perl|/usr/bin/perl|' $BIN_PATH/tmhmm 
sed -i '1 s|/usr/local/bin/perl|/usr/bin/perl|' $BIN_PATH/tmhmmformat.pl
chmod +x $BIN_PATH/decodeanhmm.Linux_x86_64

echo "Cleaning up ..."
rm -r $BASE_NAME $TARBALL

echo "Successfully installed tmhmm version $VERSION"


