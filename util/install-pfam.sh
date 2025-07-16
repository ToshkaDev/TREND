#!/bin/bash
#
# Installs the specified Pfam <database version> to <target directory>. Assumes
# that the hmmpress binary (part of HMMER3) is provided via HMMER3_BINDIR parameter.

# exit immediately if any command fails
set -e

VERSION=$1
TARGET_DIR=$2
HMMER3_BINDIR=$3

TEMP_DIR=$TARGET_DIR/tmp
mkdir -p $TEMP_DIR

PFAM_DIR="$TARGET_DIR/PfamA"
mkdir -p $PFAM_DIR

if [[ -z "$VERSION" || -z "$TARGET_DIR" ]]; then
	echo "Usage: $0 <pfam version> <target directory>"
	exit 1
fi

PFAM_HMM_NAME="Pfam-A.hmm"
GZ_PFAM_HMM_NAME="$PFAM_HMM_NAME.gz"
PFAM_HMM_URL="https://ftp.ebi.ac.uk/pub/databases/Pfam/releases/Pfam$VERSION/$GZ_PFAM_HMM_NAME"

if [[ -s "$PFAM_DIR/$PFAM_HMM_NAME" &&
	-s "$PFAM_DIR/$PFAM_HMM_NAME.h3f" &&
	-s "$PFAM_DIR/$PFAM_HMM_NAME.h3i" &&
	-s "$PFAM_DIR/$PFAM_HMM_NAME.h3m" &&
	-s "$PFAM_DIR/$PFAM_HMM_NAME.h3p" ]]; then
	echo "Pfam $VERSION database is already installed"
	exit
fi

if [[ ! -s "$PFAM_DIR/$PFAM_HMM_NAME" ]]; then
	echo "Downloading Pfam $VERSION HMM database ..."
	curl -L -o "$TEMP_DIR/$GZ_PFAM_HMM_NAME" "$PFAM_HMM_URL"
	echo "Decompressing ..."
	gunzip "$TEMP_DIR/$GZ_PFAM_HMM_NAME"
	echo "Copying to $PFAM_DIR"
	mv "$TEMP_DIR/$PFAM_HMM_NAME" $PFAM_DIR
fi

cd $PFAM_DIR

# Download MiST and TREND models
FILEID=1EHDOluhCcDy-pgbOKRVz_PbxXtKGQ7tk
MIST_TREND_NAME=Mist_Trend.hmm
TARBALL=${MIST_TREND_NAME}.tar.gz
curl -L -o "$TARBALL" "https://docs.google.com/uc?export=download&id=${FILEID}"

echo "Decompressing tarball ..."
tar xvf $TARBALL
rm $TARBALL

#Preparing models
echo "Preparing Pfam HMM database ..."
$HMMER3_BINDIR/hmmpress -f ./$PFAM_HMM_NAME

echo "Preparing Pfam + MiST_TREND HMM database ..."
PFAM_MIST_TREND_HMM_NAME=Pfam-A_and_Mist-specific.hmm
cat $PFAM_HMM_NAME $MIST_TREND_NAME > $PFAM_MIST_TREND_HMM_NAME
$HMMER3_BINDIR/hmmpress -f ./$PFAM_MIST_TREND_HMM_NAME

echo "Successfully installed Pfam $VERSION database and MiST_TREND database"