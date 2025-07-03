#!/bin/bash

set -e 

#0.4.0
#RPSBPROC_VERSION=$1  [No version anymore]
#2.7.1
BLAST_VERSION=$1
#/app
TARGET_DIR=$(realpath $2)
#/tmp
TEMP_DIR=$(realpath $3)

#if [[ -z "$RPSBPROC_VERSION" || -z "$BLAST_VERSION" || -z "$TARGET_DIR" || -z "$TEMP_DIR" ]]; then
#	echo "Usage: $0 <rpsbproc version> <blast version> <target directory> <tem directory>"
#	exit 1
#fi

if [[ -z "$BLAST_VERSION" || -z "$TARGET_DIR" || -z "$TEMP_DIR" ]]; then
	echo "Usage: $0 <blast version> <target directory> <tem directory>"
	exit 1
fi


BIN_PATH=$TARGET_DIR/bin
RPSBPROC_DATA_PATH=$TARGET_DIR/rpsbproc_data

#FTPSITE="ftp://ftp.ncbi.nih.gov/pub/mmdb/cdd"
FTPSITE="https://ftp.ncbi.nih.gov/pub/mmdb/cdd/"
RPSBPROC="RpsbProc-x64-linux"
RPSBPROC_TAR="$RPSBPROC.tar.gz"
RPSBPROC_URL="$FTPSITE/rpsbproc/$RPSBPROC_TAR"
UNTARED_DIR=export

files=("cddid.tbl.gz" "cdtrack.txt" "family_superfamily_links" "cddannot.dat.gz" "cddannot_generic.dat.gz" "bitscore_specific.txt")

if [[ ! -d $RPSBPROC_DATA_PATH ]]; then
	mkdir $RPSBPROC_DATA_PATH
	if [[ ! -d $RPSBPROC_DATA_PATH ]]; then
		echo "Cannot create data directory, give up ..." 1>&2
		exit 255
	fi
fi

echo "Downloading rpsbproc data files: "
for f in ${files[@]}; do
	echo ${f}
	curl ${FTPSITE}/${f} -o $RPSBPROC_DATA_PATH/${f} > /dev/null 2>&1
	if [[ "gz" == ${f##*\.} ]]; then
		gzip -d $RPSBPROC_DATA_PATH/${f}
	fi
done
echo "Done"

echo " "
echo "Downloading rpsbproc ..."
echo "This will take ~3 min ..."
cd $TEMP_DIR
echo "Download destination path: $(pwd)"
wget $RPSBPROC_URL
tar xzvf $RPSBPROC_TAR
cp "./$UNTARED_DIR/home/shlu/rpsbproc_linux_build/ncbi-blast-$BLAST_VERSION+-src/c++/ReleaseMT/bin/rpsbproc" ../$BIN_PATH/

echo " "
echo "Clening up ..."
rm -rf $RPSBPROC_TAR
rm -rf $UNTARED_DIR

cat  <<- __EOF__ > ../$BIN_PATH/rpsbproc.ini
[datapath]
cdd = $RPSBPROC_DATA_PATH/cddid.tbl
cdt = $RPSBPROC_DATA_PATH/cdtrack.txt
clst = $RPSBPROC_DATA_PATH/family_superfamily_links
feats = $RPSBPROC_DATA_PATH/cddannot.dat
genfeats = $RPSBPROC_DATA_PATH/cddannot_generic.dat
spthr = $RPSBPROC_DATA_PATH/bitscore_specific.txt
__EOF__






 






