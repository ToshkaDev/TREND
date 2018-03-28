#!/bin/bash


#basiclly need only input directory

program="/usr/bin/mafft --maxiterate 1000"
defaultAlgorithm=--globalpair



#algorithm options:--localpair, --genafpair, --globalpair
algorithm=$defaultAlgorithm
#number of threads
THREAD=2
#if --reorder, outorder: aligned, default: input order
REORDER_ORNOT=--reorder
INPUT_DIR=extractSequencesByGroups_Output
OUTPUT_DIR=.


usage() {
	echo "Program align all the sequences in the given directory based on the provided options"
	echo "usage:" $PROGNAME " [-i input directory -o output directory -t number of threads (default 1) -r reorder or not (default is --reorder; type 'yes' or 'no') -a algorithm (--localpair | --genafpair | --globalpair; deafult is --globalpair) | -h]"
}

initializeDirectories()  {
	while [[ -n $1 ]]; do
		case $1 in
			-a | --algorithm)	shift
						algorithm="$1"
						;;
			-i | --inputdir)	shift
						INPUT_DIR="$1"
						;;
			-o | --outputdir)	shift
						mkdir "$1"
						OUTPUT_DIR='../'"$1"
						;;
			-r | --reorder)		shift
						REORDER_ORNOT=$1
						;;
			-t | --thread)		shift
						THREAD=$1
						;;
			-h | --help)		usage
						exit
						;;
			*)			usage >&2
						exit 1
						;;
		esac
		shift
	done
	
	if [[ $REORDER_ORNOT == "no" ]]; then
		$REORDER_ORNOT = ""
	elif [[ $REORDER_ORNOT == "yes" ]]; then
		$REORDER_ORNOT = --reorder
	fi
}

main() {
	echo $OUTPUT_DIR
	if [[ -d $INPUT_DIR ]]; then
		if cd $INPUT_DIR; then
			for everyFile in *.txt; do
				$program $algorithm --thread $THREAD $REORDER_ORNOT $everyFile > $OUTPUT_DIR/${everyFile%.*}.fasta
			done
		else
			echo "cannot cd to '$INPUT_DIR'" >&2
			exit 1
		fi
	else
		echo "no such directory: '$INPUT_DIR'" >&2
		exit 1
	fi

}

initializeDirectories $@
main
