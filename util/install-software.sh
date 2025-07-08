#!/bin bash

APP_DIR=/app
TARGET_DIR=${APP_DIR}/Soft
TARGET_DIR=/home/vadim/Documents/Applications/TREND/util

HMMER_VERSION=3.4
HMMER_DIR=${TARGET_DIR}/hmmer-${HMMER_VERSION}
./install-hmmer.sh ${HMMER_VERSION} ${HMMER_DIR}


BLAST_VERSION=2.16.0
BLAST_DIR=${TARGET_DIR}/blast-${BLAST_VERSION}
./install-blast.sh ${BLAST_VERSION} ${BLAST_DIR}

#0.5.0 rpsbproc
RPSBPROC_DIR=${TARGET_DIR}/rpsbproc-0.5.0
# we provide BLAST_VERSION as rpsbproc processes the blast package results
/install-rpsbproc.sh ${BLAST_VERSION} ${RPSBPROC_DIR}


MAFFT_VERSION=7.487
MAFFT_DIR=${TARGET_DIR}/mafft-${MAFFT_VERSION}
./install-mafft.sh ${MAFFT_VERSION} ${MAFFT_DIR}

MEGA_VERSION=11.0.13-1
MEGA_DIR=${TARGET_DIR}/mega-${MEGA_VERSION}
./install-mega.sh ${MEGA_VERSION} ${MEGA_DIR}

# Install databases

PFAM_VERSION=37.4
PFAM_DIR=${TARGET_DIR}/pfam-${PFAM_VERSION}
./install-pfam.sh ${PFAM_VERSION} ${PFAM_DIR} ${HMMER_DIR}/bin

CDD_VERSION=3.21
CDD_DIR=${TARGET_DIR}/cdd-${CDD_VERSION}
./install-cdd.sh ${CDD_VERSION} ${CDD_DIR}
