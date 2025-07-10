#!/bin/bash

set -e 

#0.5.0
#RPSBPROC_VERSION=$1  [No version anymore]
BLAST_VERSION=$1
TARGET_DIR=$2

TEMP_DIR=$TARGET_DIR/tmp
mkdir -p $TEMP_DIR


if [[ -z "$BLAST_VERSION" || -z "$TARGET_DIR" || -z "$TEMP_DIR" ]]; then
	echo "Usage: $0 <blast version> <target directory> <tem directory>"
	exit 1
fi


BIN_PATH=$TARGET_DIR/bin
RPSBPROC_DATA_PATH=$TARGET_DIR/rpsbproc_data
mkdir -p $RPSBPROC_DATA_PATH $BIN_PATH

FTPSITE="https://ftp.ncbi.nih.gov/pub/mmdb/cdd/"
RPSBPROC="RpsbProc-x64-linux"
RPSBPROC_TAR="$RPSBPROC.tar.gz"
RPSBPROC_URL="$FTPSITE/rpsbproc/$RPSBPROC_TAR"
UNTARED_DIR=export

DATA_FILES=("cddid.tbl.gz" "cdtrack.txt" "family_superfamily_links.txt" "cddannot.dat.gz" "cddannot_generic.dat.gz" "bitscore_specific.txt")

echo "Downloading rpsbproc data files: "
for f in ${DATA_FILES[@]}; do
	echo $f
	curl -L -o "$RPSBPROC_DATA_PATH/$f" "${FTPSITE}/$f"
	if [[ ${f##*\.} == "gz" ]]; then
		gzip -d $RPSBPROC_DATA_PATH/$f
	fi
done
echo "Done"

echo " "
echo "Downloading rpsbproc ..."
echo "This will take ~3 min ..."
cd $TEMP_DIR
echo "Download destination path: $(pwd)"
curl -L -o "$RPSBPROC_TAR" "$RPSBPROC_URL"
tar xzvf $RPSBPROC_TAR
cp "$TEMP_DIR/RpsbProc-x64-linux/rpsbproc" ${BIN_PATH}

echo "Clening up ..."
rm -r $TEMP_DIR




 






