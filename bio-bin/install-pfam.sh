#!/bin/bash
#
# Installs the specified Pfam <database version> to <target directory>. Assumes
# that the hmmpress binary (part of HMMER3) is  in the $PATH environment variable.

# exit immediately if any command fails
set -e

VERSION=$1
TARGET_DIR=$2
TEMP_DIR=$3

HMMER3_BINDIR="$TARGET_DIR/bin"
PFAM_DIR="$TARGET_DIR/pfamA"

if [[ -z "$VERSION" || -z "$TARGET_DIR" ]]; then
	echo "Usage: $0 <pfam version> <target directory>"
	exit 1
fi

if [[ ! $VERSION =~ ^[1-9][0-9]*\.[0-9]$ ]] ; then
	echo 'Missing or invalid version!'
	exit 1
fi

PFAM_HMM_NAME="Pfam-A.hmm"
GZ_PFAM_HMM_NAME="$PFAM_HMM_NAME.gz"
PFAM_HMM_URL="ftp://ftp.ebi.ac.uk/pub/databases/Pfam/releases/Pfam$VERSION/$GZ_PFAM_HMM_NAME"

if [[ -s "$PFAM_DIR/$PFAM_HMM_NAME" &&
	-s "$PFAM_DIR/$PFAM_HMM_NAME.h3f" &&
	-s "$PFAM_DIR/$PFAM_HMM_NAME.h3i" &&
	-s "$PFAM_DIR/$PFAM_HMM_NAME.h3m" &&
	-s "$PFAM_DIR/$PFAM_HMM_NAME.h3p" ]]; then
	echo "Pfam $VERSION database is already installed"
	exit
fi

if [[ ! -s "$PFAM_DIR/$PFAM_HMM_NAME" ]]; then
	echo "Downloading Pfam $VERSION HMM database"
	wget -q -O "$TEMP_DIR/$GZ_PFAM_HMM_NAME" $PFAM_HMM_URL
	echo "Decompressing"
	gunzip "$TEMP_DIR/$GZ_PFAM_HMM_NAME"
	echo "Copying to $PFAM_DIR"
	mkdir -p $PFAM_DIR
	mv "$TEMP_DIR/$PFAM_HMM_NAME" $PFAM_DIR
fi

cd $PFAM_DIR
echo "Preparing Pfam HMM database"
$HMMER3_BINDIR/hmmpress -f ./$PFAM_HMM_NAME

echo "Successfully installed Pfam $VERSION database"