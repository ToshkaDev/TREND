#!/usr/bin/python

import sys, getopt
import os
import collections
from Bio import SeqIO

'''By deafult the he script expects the protine name to be like this:
>ribosomal protein l1|Caldisericum exile AZM16c01
wth organism name at the second position and delimeted by '|'
'''

FILE_WITH_TAXA_NAMES = "extractFromFile"
PATH = "extractSequencesByGroups_Output"
OUTPUT_FILENAME = "result.txt"
NAME_COLUMN = 1
TAXON_TO_SEQ_MAP = collections.OrderedDict()
FILENUMBER_TO_STRING_LENGTH = []
TAXON_TO_FILENUMBER = {}



alignedFilesNames = set()
taxaList = []
ribosomList=[]



USAGE = "Script concatenate alignments based on given taxonomy file and a bunch of aligned files in the given folder." + "\n" + "python" + sys.argv[0] + '''
-i folder with aligned files
[-l file with taxa]
[-c column with taxa on the name lines, if file with taxa is not present; default 2]
[-o output file name (default result.txt)]'''


def initialyze(argv):
	global FILE_WITH_TAXA_NAMES, NAME_COLUMN, PATH, OUTPUT_FILENAME
	try:
		opts, args = getopt.getopt(argv[1:],"hi:l:o:c:",["inputFileName=", "taxonList=", "outputFileName=", "nameColumn"])
	except getopt.GetoptError:
		print USAGE + " Error"
		sys.exit(2)
	for opt, arg in opts:
		if opt == '-h':
			print USAGE
			sys.exit()
		elif opt in ("-i", "--inputFileName"):
			PATH = str(arg).strip()
		elif opt in ("-l", "--taxonList"):
			FILE_WITH_TAXA_NAMES = str(arg)
		elif opt in ("-c", "--column"):
			#indeses begin from 0
			NAME_COLUMN = int(arg) - 1
		elif opt in ("-o", "--outputFileName"):
			OUTPUT_FILENAME = str(arg).strip()

	#initialize taxon maps if FILE_WITH_TAXA_NAMES is given
	if FILE_WITH_TAXA_NAMES != "extractFromFile":
		with open(FILE_WITH_TAXA_NAMES, 'r') as inputFile:
			for taxonName in inputFile:
				TAXON_TO_SEQ_MAP[taxonName.strip()] = ""
				TAXON_TO_FILENUMBER[taxonName.strip()] = -1
		
		

	
def createListOfFiles():
	global FILE_WITH_TAXA_NAMES
	os.chdir(PATH)
	filesInTheDirectory = os.listdir(os.getcwd())
	firstFile=True
	print filesInTheDirectory
	for fileName in filesInTheDirectory:
		if fileName.split(".")[1] == "fasta":
			alignedFilesNames.add(fileName)
		#if FILE_WITH_TAXA_NAMES is not given take the first file, extract taxa names and initialize taxon maps
		if FILE_WITH_TAXA_NAMES == "extractFromFile" and firstFile:
			print "here"
			with open(fileName, "r") as firstOpennedFile:
				for record in SeqIO.parse(firstOpennedFile, "fasta"):
					taxonName = record.description.strip().split("|")[NAME_COLUMN]
					TAXON_TO_SEQ_MAP[taxonName] = ""
					TAXON_TO_FILENUMBER[taxonName] = -1
			firstFile = False
			FILE_WITH_TAXA_NAMES = "Processed"
			

def concatenate():
	fileNumberCounter = -1
	isFirstFile = True
   
	for everyFile in alignedFilesNames:
		currentFile = open(everyFile, "r")
		fileNumberCounter+=1

		for record in SeqIO.parse(currentFile, "fasta"):
			taxonOfSequence = record.description.strip().split("|")[NAME_COLUMN]
			sequence = str(record.seq).strip()

			difference = fileNumberCounter - TAXON_TO_FILENUMBER[taxonOfSequence]
			numberOfDashes = sum(FILENUMBER_TO_STRING_LENGTH[fileNumberCounter-difference+1:fileNumberCounter])

			TAXON_TO_SEQ_MAP[taxonOfSequence] = TAXON_TO_SEQ_MAP[taxonOfSequence] + "-"*numberOfDashes + sequence
			TAXON_TO_FILENUMBER[taxonOfSequence] = fileNumberCounter
			

		currentFile.close()
		FILENUMBER_TO_STRING_LENGTH.append(len(sequence))
		isFirstFile = False
		
		
def saveResult():
	os.chdir("..")
	with open(OUTPUT_FILENAME, 'w') as outputFile:
		for taxon, seq in TAXON_TO_SEQ_MAP.items():
			outputFile.write(">" + taxon.replace(" ", "_") + "\n")
			outputFile.write(seq + "\n")
		
def main(argv):
	initialyze(argv)
	createListOfFiles()
	concatenate()
	saveResult()
	
		
		
main(sys.argv)
	
			
		




