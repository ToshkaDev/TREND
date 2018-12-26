#!/bin/bash
#
# Installs the specified Pfam <database version> to <target directory>. Assumes
# that the hmmpress binary (part of HMMER3) is  in the $PATH environment variable.

# exit immediately if any command fails
set -e

VERSION=$1
TARGET_DIR=$2
HMMER3_BINDIR=$3

if [[ -z "$VERSION" || -z "$TARGET_DIR" || -z "$HMMER3_BINDIR" ]]; then
	echo "Usage: $0 <pfam version> <target directory> <hmmer3 bindir>"
	exit 1
fi

if [[ ! $VERSION =~ ^[1-9][0-9]*\.[0-9]$ ]] ; then
	echo 'Missing or invalid version!'
	exit 1
fi

PFAM_HMM_NAME="Pfam-A.hmm"
GZ_PFAM_HMM_NAME="$PFAM_HMM_NAME.gz"
PFAM_HMM_URL="ftp://ftp.ebi.ac.uk/pub/databases/Pfam/releases/Pfam$VERSION/$GZ_PFAM_HMM_NAME"

if [[ -s "$TARGET_DIR/$PFAM_HMM_NAME" &&
	-s "$TARGET_DIR/$PFAM_HMM_NAME.h3f" &&
	-s "$TARGET_DIR/$PFAM_HMM_NAME.h3i" &&
	-s "$TARGET_DIR/$PFAM_HMM_NAME.h3m" &&
	-s "$TARGET_DIR/$PFAM_HMM_NAME.h3p" ]]; then
	echo "Pfam $VERSION database is already installed"
	exit
fi

if [[ ! -s "$TARGET_DIR/$PFAM_HMM_NAME" ]]; then
	echo "Downloading Pfam $VERSION HMM database"
	wget -q -O "/tmp/$GZ_PFAM_HMM_NAME" $PFAM_HMM_URL
	echo "Decompressing"
	gunzip "/tmp/$GZ_PFAM_HMM_NAME"
	echo "Copying to $TARGET_DIR"
	mkdir -p $TARGET_DIR
	mv "/tmp/$PFAM_HMM_NAME" $TARGET_DIR
fi

cd $TARGET_DIR
echo "Preparing Pfam HMM database"
$HMMER3_BINDIR/hmmpress -f ./$PFAM_HMM_NAME

echo "Successfully installed Pfam $VERSION database"