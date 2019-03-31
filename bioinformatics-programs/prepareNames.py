#!/usr/bin/python
import sys, getopt
import re
from ete2 import Tree
from Bio import SeqIO
from Bio import Entrez
import time
Entrez.email = "A.N.Other@example.com"
SLEEP_TIME_FOR_NCBI_REQUEST = 0.25

INPUT_FILE = None
OUTPUT_FILE = None
INPUT_FILE_TREE = None
OUTPUT_FILE_TREE = None
FETCH_FROM_IDS = False
FETCH_FROM_TREE = False


USAGE = "\n\nThe script makes sequence names in the file newick friendly.\n\n" + \
	"python 	" + sys.argv[0] + '''
	-h                     - help
	-i || --ifile          - input file with sequences
	-s || --sfile          - input file with phylogenetic tree in newick format
	-o || --ofile          - output file with sequences with changed names
	-n || --nfile          - output file with tree with changed names of leaves
	-f || --fetchFromIds   - fetch sequences from ids or not: "true" or "false". Default is "false"
	-t || --fetchFromTree  - fetch sequences from tree leaves names or not: "true" or "false". Default is "false"
	'''

def initialize(argv):
	global INPUT_FILE, INPUT_FILE_TREE, OUTPUT_FILE, OUTPUT_FILE_TREE, FETCH_FROM_IDS, FETCH_FROM_TREE
	try:
		opts, args = getopt.getopt(argv[1:],"hi:s:o:n:f:t:",["help", "ifile=", "sfile=", "ofile=", "nfile=", "fetchFromIds=", "fetchFromTree="])
		if len(opts) == 0:
			raise getopt.GetoptError("Options are required\n")
	except getopt.GetoptError as e:
		print "===========ERROR==========\n " + str(e) + USAGE
		sys.exit(2)
	try:
		for opt, arg in opts:
			if opt in ("-h", "--help"):
				print USAGE
				sys.exit()
			elif opt in ("-i", "--ifile"):
				INPUT_FILE = str(arg).strip()
			elif opt in ("-o", "--ofile"):
				OUTPUT_FILE = str(arg).strip()
			elif opt in ("-s", "--sfile"):
				INPUT_FILE_TREE = str(arg).strip()
			elif opt in ("-n", "--nfile"):
				OUTPUT_FILE_TREE = str(arg).strip()
			elif opt in ("-f", "--fetchSeqs"):
				if str(arg).strip() == "true":
					FETCH_FROM_IDS = True
			elif opt in ("-t", "--fetchFromTree"):
				if str(arg).strip() == "true":
					FETCH_FROM_TREE = True
	except Exception as e:
		print "===========ERROR==========\n " + str(e) + USAGE
		sys.exit(2)

REGEX_VOID = re.compile(r"(\(|\)|:|,|}|{|'|/|]|\[|\\)")
REGEX_UNDERSCORE = re.compile(r"( |\|)")
REGEX_UNDERSCORE_MULTIPLE = re.compile(r"_{2,}")
REGEX_VOID_SUBST = ""
REGEX_UNDERSCORE_SUBST = "_"
REGEX_LEAF_NAME_1 = re.compile(r"(\('[^\(].+?':|,'[^\(].+?':)")
REGEX_LEAF_NAME_2 = re.compile(r"(\([^\(].+?:|,[^\(].+?:)")
REGEX_NUMBER_UNDERSCORE = re.compile(r"^\d+_")

# basic name changer
def getChangedName(line):
	line = REGEX_VOID.sub(REGEX_VOID_SUBST, line)
	line = REGEX_UNDERSCORE.sub(REGEX_UNDERSCORE_SUBST, line)
	return REGEX_UNDERSCORE_MULTIPLE.sub(REGEX_UNDERSCORE_SUBST, line)

# Default case for Tree
def getChangedNamesForTree():
	if not INPUT_FILE_TREE or not OUTPUT_FILE_TREE:
		print "Both input file name with tree in newick format and output file name should be provided!"
		return
	else:
		print "Changing tree leaves names"
	with open(INPUT_FILE_TREE, "r") as inputFile:
		tree = inputFile.read()
	iterObject1 = re.finditer(REGEX_LEAF_NAME_1, tree)
	iterObject2 = re.finditer(REGEX_LEAF_NAME_2, tree)
	for match in iterObject1:
		tree = tree.replace(match.group()[1:-1], getChangedName(match.group()[2:-2]))

	for match in iterObject2:
		tree = tree.replace(match.group()[1:-1], getChangedName(match.group()[1:-1]))
	#check if we can read the tree
	treeObject = Tree(tree)
	with open(OUTPUT_FILE_TREE, "w") as outputFile:
		outputFile.write(tree)
	return treeObject

# Default case for Sequences
def getChangedNamesForSeqs(handle=None):
	if not INPUT_FILE or not OUTPUT_FILE:
		print "Both input file name with sequences and output file name should be provided!"
		return
	else:
		print "Changing sequence names"
	with open(OUTPUT_FILE, "w") as outputFile:
		if not handle:
			with open(INPUT_FILE, "r") as inputFile:
				for line in inputFile:
					outputFile.write(getChangedName(line))
		else:
			for eachRecord in SeqIO.parse(handle, "fasta"):
				outputFile.write(">" + getChangedName(eachRecord.description) + "\n")
				outputFile.write(str(eachRecord.seq) + "\n")

# A case with the list of Ids for Sequences
def fetchMultipSequencesFromNcbiAndChangeNames():
	idList = []
	handle = None
	with open(INPUT_FILE, "r") as inputFile:
		for line in inputFile:
			idList.append(line.strip())
	try:
		#handle = Entrez.efetch(db="protein", id="OYV75139.1,ACR67403.1", rettype="fasta", retmode="text")
		handle = Entrez.efetch(db="protein", id=",".join(idList), rettype="fasta", retmode="text")
		getChangedNamesForSeqs(handle)
	finally:
		if handle:
			handle.close()

#=====================#
### Tree procesing: fetching sequences from NCBI using tree leaves names ###

def fetchSequencesForTree(treeObject):
	proteinIds1 = list()
	proteinIds2 = list()
	originalNames = list()
	terminals = treeObject.get_leaves()
	for protein in terminals:
		originalProteinName = protein.name.strip("'")
		fullProteinName = REGEX_NUMBER_UNDERSCORE.sub("", originalProteinName)
		partProteinName = "_".join(fullProteinName.split("_")[0:2]).strip()
		proteinIds1.append(partProteinName)
 		partProteinName = fullProteinName.split("_")[0].strip()
		proteinIds2.append(partProteinName)
		originalNames.append(originalProteinName)
	startFetchingSequencesForTree([proteinIds1, proteinIds2, originalNames])

def startFetchingSequencesForTree(idList):
	with open(OUTPUT_FILE, "w") as outputFile:
		for proteinIdIndex in xrange(len(idList[0])):
			record = None
			proteinId = idList[0][proteinIdIndex]
			originalName = idList[2][proteinIdIndex]
			try:
				record = fetchOneSequenceFromNcbi(proteinId)
				if not record:
					print "Not record"
					proteinId = idList[1][proteinIdIndex]
					record = fetchOneSequenceFromNcbi(proteinId)
			except Exception, e:
				print "First Exception"
				print e
				proteinId = idList[1][proteinIdIndex]
				try:
					record = fetchOneSequenceFromNcbi(proteinId)
				except Exception, e:
					print "Second Exception"
					continue
			if record:
				outputFile.write(">" + originalName + "\n")
				outputFile.write(str(record.seq) + "\n")

def fetchOneSequenceFromNcbi(proteinId):
	record = None
	handle = None
	time.sleep(SLEEP_TIME_FOR_NCBI_REQUEST)
	try:
		handle = Entrez.efetch(db="protein", id=proteinId, rettype="fasta", retmode="text")
		record = SeqIO.parse(handle, "fasta")
		record = record.next()
	finally:
		if handle:
			handle.close()
	return record


def main(argv):
	initialize(argv)
	if FETCH_FROM_IDS:
		fetchMultipSequencesFromNcbiAndChangeNames()
	else:
		getChangedNamesForSeqs()

	treeObject = getChangedNamesForTree()
	if FETCH_FROM_TREE:
		fetchSequencesForTree(treeObject)


if __name__ == "__main__":
	main(sys.argv)