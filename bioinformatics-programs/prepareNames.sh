#!/bin/bash
#author: Vadim M. Gumerov; 08/29/2017


PROGNAME=$(basename $0)
input_dir="../bioinformatics-programs-workingDir/files"
output_dir="../new_output"
name_delim="|"
name_column=2
tempExt=temp
finalExt=fa



numOfLetters=26

fromIntTOLetterArray=({A..Z})
declare -A letterCombsArray
declare -a letterCombsArray2

usage() {
	echo "usage:" $PROGNAME " [-i input directory -o output directory -d delimeter -c column with unique genome locus | -h]"
}

initializeDirectories()  {
	while [[ -n $1 ]]; do
		case $1 in
			-i | --inputdir)	shift
						input_dir="$1"
						;;
			-o | --outputdir)	shift
						output_dir="$1"
						;;
			-d | --delim)		shift
						name_delim=$1
						;;
			-c | --column)		shift
						name_column=$(($1+1))
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
	
	echo "name_delim: $name_delim"
	echo "name_column: $name_column"
	echo "input_dir: $input_dir"
	echo "output_dir $output_dir"
	
	if ! [[ -d "$input_dir" ]]; then
		echo "Input directory does not exist!"
		echo "Aborting!"
		exit 1
	fi 
	
	if ! [[ -d "$output_dir" ]]; then
		mkdir "$output_dir"
	fi	
}


getRandomThreeLetterStr() {	
	local newComb
	num1=$[ ( $RANDOM % $numOfLetters ) ]
	num2=$[ ( $RANDOM % $numOfLetters ) ]
	num3=$[ ( $RANDOM % $numOfLetters ) ]
	let1=${fromIntTOLetterArray[$num1]}
	let2=${fromIntTOLetterArray[$num2]}
	let3=${fromIntTOLetterArray[$num3]}
	newComb=$let1$let2$let3

	while ! [[ ${letterCombsArray[$newComb]} == "" ]]; do
		num1=$[ ( $RANDOM % $numOfLetters ) ]
		num2=$[ ( $RANDOM % $numOfLetters ) ]
		num3=$[ ( $RANDOM % $numOfLetters ) ]
		let1=${fromIntTOLetterArray[$num1]}
		let2=${fromIntTOLetterArray[$num2]}
		let3=${fromIntTOLetterArray[$num3]}
		newComb=$let1$let2$let3
	done
	echo $newComb

}


fillInLetterCombsArray() {
	numOfFiles=$(ls $input_dir | wc -l)
	local arrayInd=0
	for (( i=0; i<$numOfFiles; i++ )); do
		newComb=$(getRandomThreeLetterStr)
		letterCombsArray[$newComb]=$newComb
		letterCombsArray2[$((arrayInd++))]=$newComb
	done
}


getRidOfSpaces() {
	for eachFile in "$input_dir"/*; do
		echo "eachFile " $eachFile
		newName=${eachFile// /_}
		cp "$eachFile" "$output_dir"/$(basename ${newName%.*}).$tempExt		
	done
}


main() { 
	fillInLetterCombsArray
	getRidOfSpaces
	
	local inde=0

	echo "Following files are created: "
	for everyFile in "$output_dir"/*.$tempExt; do
		/usr/bin/awk -v letterComb=${letterCombsArray2[$inde]} -v nameDelim="$name_delim" -v nameColumn="$name_column" \
		'BEGIN{FS=nameDelim}{if (substr($1, 1,1) == ">") {newstring=""; for (i=1; i<=NF; i++) {\
		if (i != nameColumn) {newstring=newstring""nameDelim""$i} \
		else {newstring=newstring""nameDelim""letterComb"_"$i}}; print newstring} else {print $0}}' $everyFile | /bin/sed "s/$name_delim//" > "$output_dir"/$(basename ${everyFile%.*}).$finalExt
		echo "$output_dir"/$(basename ${everyFile%.*}).$finalExt
		echo $((inde++))
	done
	rm "$output_dir"/*.temp
}

initializeDirectories $@
main





















