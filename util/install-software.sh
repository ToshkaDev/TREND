#!/bin/bash

APP_DIR=/app
TARGET_DIR=${APP_DIR}/Soft

HMMER_VERSION=3.4
HMMER_DIR=${TARGET_DIR}/hmmer-${HMMER_VERSION}
./util/install-hmmer.sh ${HMMER_VERSION} ${HMMER_DIR}

BLAST_VERSION=2.16.0
BLAST_DIR=${TARGET_DIR}/blast-${BLAST_VERSION}
./util/install-blast.sh ${BLAST_VERSION} ${BLAST_DIR}

RPSBPROC_DIR=${TARGET_DIR}/rpsbproc-0.5.0
./util/install-rpsbproc.sh ${RPSBPROC_DIR}

MAFFT_VERSION=7.487
MAFFT_DIR=${TARGET_DIR}/mafft-${MAFFT_VERSION}
./util/install-mafft.sh ${MAFFT_VERSION} ${MAFFT_DIR}

MEGA_VERSION=10.2.6-1
MEGA_DIR=${TARGET_DIR}/mega-${MEGA_VERSION}
./util/install-mega.sh ${MEGA_VERSION} ${MEGA_DIR}

FASTTREE_VERSION=2.2
FASTTREE_DIR=${TARGET_DIR}/fasttree-${FASTTREE_VERSION}
./util/install-fasttree.sh ${FASTTREE_VERSION} ${FASTTREE_DIR}

CDHIT_VERSION=4.8.1
CDHIT_DIR=${TARGET_DIR}/cdhit-${CDHIT_VERSION}
./util/install-cdhit.sh ${CDHIT_VERSION} ${CDHIT_DIR}

TMHMM_VERSION=2.0c
TMHMM_DIR=${TARGET_DIR}/tmhmm-${TMHMM_VERSION}
./util/install-tmhmm.sh ${TMHMM_VERSION} ${TMHMM_DIR}

# Install databases
PFAM_VERSION=37.4
PFAM_DIR=${TARGET_DIR}/pfam-${PFAM_VERSION}
./util/install-pfam.sh ${PFAM_VERSION} ${PFAM_DIR} ${HMMER_DIR}/bin

CDD_VERSION=3.21
CDD_DIR=${TARGET_DIR}/cdd-${CDD_VERSION}
./util/install-cdd.sh ${CDD_VERSION} ${CDD_DIR}
