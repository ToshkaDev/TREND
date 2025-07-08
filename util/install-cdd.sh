#!/bin/bash

set -e

VERSION=$1
CDD_PSSMS_DIR=$2
mkdir -p $CDD_PSSMS_DIR

if [ -z "$VERSION" ] || [ -z "$CDD_PSSMS_DIR" ]; then
	echo "Usage: $0 <CDD PSSMs version> <target directory>"
	exit 1
fi

PSSM_TARS=("Cdd_LE.tar.gz" "Cdd_NCBI_LE.tar.gz" "Cog_LE.tar.gz" "Kog_LE.tar.gz" "Prk_LE.tar.gz" "Smart_LE.tar.gz" "Tigr_LE.tar.gz")
#PSSM_TARS=("Smart_LE.tar.gz" "Prk_LE.tar.gz")


CDD_PSSM_URL=https://ftp.ncbi.nih.gov/pub/mmdb/cdd/little_endian/

if [ -n "$(ls -A $CDD_PSSMS_DIR)" ]; then
	echo "CDD $VERSION PSSMs database is already installed"
	exit
else 
	echo "Downloading CDD $VERSION PSSMs database ..."
	for f in ${PSSM_TARS[@]}; do
		echo $f
		curl ${CDD_PSSM_URL}/$f -o $CDD_PSSMS_DIR/$f > /dev/null 2>&1
		tar xvf $CDD_PSSMS_DIR/$f -C $CDD_PSSMS_DIR
		echo "Delete the tar compressed file $f ..."
		rm $CDD_PSSMS_DIR/$f
	done
fi

echo "Succesfully installed CDD PSSMs database."