#!/usr/bin/python

import fileinput, sys, getopt
from Bio import SeqIO

INPUT_FILENAME1="names.txt"
INPUT_FILENAME2="names_and_sequences.fa"

OUTPUT_FILENAME="result.txt"
DELIM1=None
COLUMN_NUM1=None
DELIM2=None
COLUMN_NUM2=None

protein_names = set()
result_proteins = []

USAGE = "Script extractes sequences from the second file according to sequence names in the first file." + "\n" + sys.argv[0] + ''' [
-i first input file name (default -names.txt) 
-s  second input file name (default -"names_and_sequences.fa") 
-o output file name (default - result.txt) 
-d delimeter in the first file 
-c column number in the first file
-t delimeter in the second file 
-l column number in the second file]
When delimeter and column numbers are not specified the whole name will be used.'''


def initialyze(argv):
	global INPUT_FILENAME1, INPUT_FILENAME2, OUTPUT_FILENAME, DELIM1, COLUMN_NUM1, DELIM2, COLUMN_NUM2
	try:
		opts, args = getopt.getopt(argv[1:],"hi:s:o:d:c:t:l:",["inputFileName1=", "inputFileName2=", "outputFileName=", "delimeter1=", "columnNumber1=", "delimeter2=", "columnNumber2="])
	except getopt.GetoptError:
		print USAGE + " Error"
		sys.exit(2)
	for opt, arg in opts:
		if opt == '-h':
			print USAGE
			sys.exit()
		elif opt in ("-i", "--inputFileName1"):
			INPUT_FILENAME1 = str(arg).strip()
		elif opt in ("-s", "--inputFileName2"):
			INPUT_FILENAME2 = str(arg).strip()
		elif opt in ("-o", "--outputFileName"):
			OUTPUT_FILENAME = str(arg).strip()
		elif opt in ("-d", "--delimeter1"):
			DELIM1 = str(arg)
		elif opt in ("-c", "--columnNumber1"):
			COLUMN_NUM1 = int(arg)
		elif opt in ("-t", "--delimeter2"):
			DELIM2 = str(arg)
		elif opt in ("-l", "--columnNumber2"):
			COLUMN_NUM2 = int(arg)

def process():
	with open(INPUT_FILENAME1, 'r') as inputFile1:
		for line in inputFile1:
			if DELIM1 != None and COLUMN_NUM1 != None:
				line = line.split(DELIM1)[COLUMN_NUM1].strip()
			else:
				line = line.strip()
			protein_names.add(line)

	with open(OUTPUT_FILENAME, "w") as outputFile:
		with open(INPUT_FILENAME2, "r") as inputFile2:
			for record in SeqIO.parse(inputFile2, "fasta"):
				if DELIM2 != None and COLUMN_NUM2 != None:
					currentProtName = record.description.split(DELIM2)[COLUMN_NUM2].strip()
				else:
					currentProtName = record.description.strip()
				if currentProtName in protein_names:
					outputFile.write(">" + currentProtName + "\n")
					outputFile.write(str(record.seq) + "\n")


def main(argv):
	initialyze(argv)
	process()
	
main(sys.argv)
	
	
	
