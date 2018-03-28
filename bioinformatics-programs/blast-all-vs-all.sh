#!/bin/bash
#author: Vadim M. Gumerov; 08/29/2017
#export PS4='$LINENO + '

PROGNAME=$(basename $0)
input_dir="../bioinformatics-programs-workingDir/prepareNames_output"
output_dir="../bioinformatics-programs-workingDir/blast_output"

fileExt=fa
finalExt=txt
workingDir=""

usage() {
	echo "usage: " $PROGNAME " [-i input directory -o output directory -d delimeter | -h ]"
}

initializeDirectories() {
	while [[ -n $1 ]]; do
		case $1 in
			-i | --inputdir)	shift
						input_dir=$1
						;;
			-o | --outputdir)	shift
						output_dir=$1
						;;
			-w | --workingDir) 	shift;
						workingDir=$1
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
	
	if ! [[ -d "$input_dir" ]]; then
		echo "Input directory does not exist!"
		echo "Aborting!"
		exit 1
	fi 

	if ! [[ -d "$output_dir" ]]; then
		mkdir "$output_dir"
	fi 
}



main() {
	declare -A arrayOfFiles
	
	for eachFile in "$input_dir"/*.$fileExt; do
		#newName=${eachFile// /_}
		#mv "$eachFile" $newName	
		echo "eachfilr "$workingDir$eachFile
		makeblastdb -in $workingDir$eachFile -parse_seqids -dbtype prot
		arrayOfFiles[$workingDir$eachFile]=$workingDir$eachFile
	done
		
	for eachFile in ${arrayOfFiles[@]}; do
		for anotherFile in ${arrayOfFiles[@]}; do
			if [[ $eachFile != $anotherFile ]]; then
				blastp -num_threads 4 -db $anotherFile -query $eachFile -outfmt "6 std qcovs"  -out $workingDir"$output_dir"/$(basename ${eachFile%.*})_vs_$(basename ${anotherFile%.*}).$finalExt
			fi
		done
	done
}

initializeDirectories $@
main
	 
	
