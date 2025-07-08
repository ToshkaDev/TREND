#!/bin/bash

set -e

VERSION=$1
TARGET_DIR=$2
BIN_PATH=$TARGET_DIR/bin
mkdir -p $TARGET_DIR
mkdir -p $BIN_PATH

if [[ -z "$VERSION" || -z "$TARGET_DIR" ]]; then
	echo "Usage: $0 <FastTree version> <target directory>"
	exit 1
fi

FASTTREE_URL="http://www.microbesonline.org/fasttree/FastTreeMP"

echo "Downloading FASTTREE ..."

curl -L -o "$BIN_PATH/FastTreeMP" "$FASTTREE_URL"

echo "Successfully installed FASTTREE version $VERSION"