#!/usr/bin/python
# Author: Vadim Gumerov
# 03.04/2019
import sys, getopt
import re
from ete2 import Tree
from Bio import SeqIO
from Bio import Entrez
import time
import collections
import urllib
import json
import math
import multiprocessing
import time
#pip install timeout-decorator
import timeout_decorator

manager = multiprocessing.Manager()
Entrez.email = "A.N.Other@example.com"
# 2 sec
ENTREZ_EFETCH_TIMEOUT = 15
SLEEP_TIME_FOR_NCBI_REQUEST = 0.25

INPUT_FILE = None
OUTPUT_FILE = None
OUTPUT_FILE_WITH_DASHES = None
INPUT_FILE_TREE = None
OUTPUT_FILE_TREE = None
FETCH_FROM_IDS = False
FETCH_FROM_TREE = False
REMOVE_DASHES = True

REGEX_VOID = re.compile(r"(\(|\)|:|,|}|{|'|]|\[|\\)")
REGEX_UNDERSCORE = re.compile(r"( |/|\||;|=)")
REGEX_UNDERSCORE_MULTIPLE = re.compile(r"_{2,}")
REGEX_VOID_SUBST = ""
REGEX_UNDERSCORE_SUBST = "_"
REGEX_LEAF_NAME_1 = re.compile(r"(\('[^\(].+?':|,'[^\(].+?':)")
REGEX_LEAF_NAME_2 = re.compile(r"(\([^\(].+?:|,[^\(].+?:)")
REGEX_NUMBER_UNDERSCORE = re.compile(r"^\d+_")

MIST_BASE_URL_GENOMES = "https://mib-jouline-db.asc.ohio-state.edu/v1/genes?search="
MIST_BASE_URL_MAGS = "https://metagenomes.asc.ohio-state.edu/v1/genes?search="
URLS = [MIST_BASE_URL_GENOMES, MIST_BASE_URL_MAGS]
ASEQ_SEQUENCE_FIELD = "&fields=id,stable_id&fields.Aseq=sequence&fields.Component=definition"
NUMBER_OF_PROCESSES = 30
FETCH_FROM_NCBI = True
FETCH_FROM_MIST = False

USAGE = "\nThe script makes sequence names and/or tree leaves names newick friendly.\n" + \
	"Moreover the script fetch sequences by id from the list of ids or from the tree leves.\n\n" + \
	"python 	" + sys.argv[0] + '''
	-h || --help            - help
	-i || --ifile           - input file with sequences or id list
	-s || --sfile           - input file with phylogenetic tree in newick format
	-o || --ofile           - output file with sequences with changed names (and retrieved seqs if id list was given)
	-n || --nfile           - output file with tree with changed names of leaves
	-b || --bfile           - output file with sequences with changed names but not removed dashes; this is a ready alignment
	[-f || --fetchFromIds]  - fetch sequences from ids or not: "true" or "false". Default is "false".
							 Only "-f" or "-t" (below) can be used. Not both at the same time.
	[-t || --fetchFromTree] - fetch sequences from tree leaves names or not: "true" or "false". Default is "false"
	[-c || --proc_num]      - number of processes to run simultaneously, default is 30. The number is big bacause the process is not CPU-intensive
	[-m || --fetchFromMist] - fetch or not from Mist: "true" or "false". Default is "false".
	[-r || --fetchFromNCBI] - fetch or not from NCBI: "true" or "false". Default is "true".
	[-d || --removeDashes]  - remove or not dashes in sequences: "true" or "false". Default is "true".
	'''

def initialize(argv):
	global INPUT_FILE, INPUT_FILE_TREE, OUTPUT_FILE, OUTPUT_FILE_WITH_DASHES, OUTPUT_FILE_TREE, FETCH_FROM_IDS, FETCH_FROM_TREE
	global NUMBER_OF_PROCESSES, FETCH_FROM_MIST, FETCH_FROM_NCBI, REMOVE_DASHES
	try:
		opts, args = getopt.getopt(argv[1:],"hi:s:o:b:n:f:t:c:m:r:d:",["help", "ifile=", "sfile=", "ofile=", "nfile=", "bfile=", "fetchFromIds=", \
			"fetchFromTree=", "proc_num=", "fetchFromMist=", "fetchFromNCBI=", "removeDashes="])
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
			elif opt in ("-b", "--bfile"):
				OUTPUT_FILE_WITH_DASHES = str(arg).strip()
			elif opt in ("-s", "--sfile"):
				INPUT_FILE_TREE = str(arg).strip()
			elif opt in ("-n", "--nfile"):
				OUTPUT_FILE_TREE = str(arg).strip()
			elif opt in ("-f", "--fetchSeqs"):
				if str(arg).strip().lower() == "true":
					FETCH_FROM_IDS = True
			elif opt in ("-t", "--fetchFromTree"):
				if str(arg).strip().lower() == "true":
					FETCH_FROM_TREE = True
			elif opt in ("-m", "--fetchFromMist"):
				if str(arg).strip().lower() == "true":
					FETCH_FROM_MIST = True
			elif opt in ("-r", "--fetchFromNCBI"):
				if str(arg).strip().lower() == "false":
					FETCH_FROM_NCBI = False
			elif opt in ("-c", "--proc_num"):
				NUMBER_OF_PROCESSES = int(str(arg).strip())
			elif opt in ("-d", "--removeDashes"):
				if str(arg).strip().lower() == "false":
					REMOVE_DASHES = False
	except Exception as e:
		print "===========ERROR==========\n " + str(e) + USAGE
		sys.exit(2)

# basic name changer
def getChangedName(line):
	line = REGEX_VOID.sub(REGEX_VOID_SUBST, line)
	line = REGEX_UNDERSCORE.sub(REGEX_UNDERSCORE_SUBST, line)
	return REGEX_UNDERSCORE_MULTIPLE.sub(REGEX_UNDERSCORE_SUBST, line.rstrip("."))

# Default case for Tree
def getChangedNamesForTree():
	if not isInOutOk(INPUT_FILE_TREE, OUTPUT_FILE_TREE, True):
		return
	with open(INPUT_FILE_TREE, "r") as inputFile:
		tree = inputFile.read()
	iterObject1 = re.finditer(REGEX_LEAF_NAME_1, tree)
	iterObject2 = re.finditer(REGEX_LEAF_NAME_2, tree)
	iterObject3 = re.finditer(REGEX_NUMBER_UNDERSCORE, tree)
	for match in iterObject1:
		tree = tree.replace(match.group()[1:-1], getChangedName(match.group()[2:-2]))

	for match in iterObject2:
		tree = tree.replace(match.group()[1:-1], getChangedName(match.group()[1:-1]))

	#check if we can read the tree
	treeObject = Tree(tree)
	with open(OUTPUT_FILE_TREE, "w") as outputFile:
		outputFile.write(tree)
	return treeObject

# Save sequences by Id: this is a final step
def getChangedNamesForSeqsAndSave(handle=False, proteinIdToSeq=False, proteinIdToTrueId=False):
	if not isInOutOk(INPUT_FILE, OUTPUT_FILE):
		return
	savedIds = set()
	with open(OUTPUT_FILE, "w") as outputFile:
		# default case
		if not handle and not proteinIdToSeq:
			# if an output file for the alignment with unchanged sequences is not provided:
			if not OUTPUT_FILE_WITH_DASHES:
				with open(INPUT_FILE, "r") as inputFile:
					for sequence in SeqIO.parse(inputFile, "fasta"):
						protSeq = str(sequence.seq)
						if REMOVE_DASHES:
							protSeq = protSeq.replace("-", "")
						outputFile.write(">" + getChangedName(sequence.description) + "\n")
						outputFile.write(protSeq + "\n")
			# else save unchanged sequences to the file provided for the alignment and save sequences after removing dashes
			# to a separate file:
			else:
				with open(INPUT_FILE, "r") as inputFile, open(OUTPUT_FILE_WITH_DASHES, "w") as outputWDashes:
					for sequence in SeqIO.parse(inputFile, "fasta"):
						protSeq = str(sequence.seq)
						outputWDashes.write(">" + getChangedName(sequence.description) + "\n")
						outputWDashes.write(protSeq + "\n")
						if REMOVE_DASHES:
							protSeq = protSeq.replace("-", "")
						outputFile.write(">" + getChangedName(sequence.description) + "\n")
						outputFile.write(protSeq + "\n")
		if handle:
			# after retrieving seqeunces by Id from NCBI
			print("Handle is present")
			for eachRecord in SeqIO.parse(handle, "fasta"):
				if eachRecord.id not in savedIds:
					protSeq = str(eachRecord.seq)
					outputFile.write(">" + getChangedName(eachRecord.description) + "\n")
					outputFile.write(protSeq + "\n")
					savedIds.add(eachRecord.id)
		if proteinIdToSeq:
			# after retrieving seqeunces by Id from Mist
			for proteinName, seq in proteinIdToSeq.items():
				if proteinIdToTrueId[proteinName] not in savedIds:
					protSeq = seq
					outputFile.write(">" + getChangedName(proteinName) + "\n")
					outputFile.write(protSeq + "\n")
					savedIds.add(proteinIdToTrueId[proteinName])

####===================================================#####
### Fetching sequences from NCBI using input Id list ###

def prepareIdListFromInput():
	proteinIds = set()
	with open(INPUT_FILE, "r") as inputFile:
		for line in inputFile:
			record = line.strip()
			if len(record):
				proteinIds.add(record)
	proteinIdsMultiProc = manager.list(proteinIds.copy())
	return (proteinIds, proteinIdsMultiProc)


def fetchNamesAndSave():
	handle = None
	proteinIdToSeq = None
	proteinIdToTrueId = None
	proteinIds, proteinIdsMultiProc = prepareIdListFromInput()
	try:
		if FETCH_FROM_MIST:
			proteinIdToSeq, proteinIdToTrueId = fetchFromMistByIds(proteinIdsMultiProc)
			print("Fetching from MiST finished OK.")
	except Exception, e:
		print("Error while fetching from MiST.")
		print (e)
	finally:
		try:
			if FETCH_FROM_NCBI:
				print("Will try to fetch from NCBI.")
				handle = getHandleOfFetchedSequencesFromNcbi(proteinIds)
				print("Fetching from NCBI finished OK.")
			if FETCH_FROM_MIST and proteinIdToSeq:
				getChangedNamesForSeqsAndSave(handle, proteinIdToSeq, proteinIdToTrueId)
			else:
				getChangedNamesForSeqsAndSave(handle=handle)
		except Exception, e:
			print("Error while fetching from NCBI.")
			print (e)
			if FETCH_FROM_MIST and proteinIdToSeq:
				getChangedNamesForSeqsAndSave(handle, proteinIdToSeq, proteinIdToTrueId)
		finally:
			if handle:
				handle.close()


# ============== Fetch from MiST# ============== #
def fetchFromMistByIds(proteinIds, proteinIdsToOrigNames=None):
	if not proteinIds:
		return None
	print("Fetching from MiST using ids")
	proteinIdToSeq = manager.dict()
	proteinIdToTrueId = manager.dict()
	elementsForOneThread = int(math.ceil(len(proteinIds)/float(NUMBER_OF_PROCESSES)))
	processes = list()
	startIndex = 0
	endIndex = elementsForOneThread
	for ind in xrange(NUMBER_OF_PROCESSES):
		if startIndex <= len(proteinIds):
			process = multiprocessing.Process(target=fetchFromMist, \
				args=(proteinIdToSeq, proteinIdToTrueId, proteinIds[startIndex:endIndex], proteinIdsToOrigNames,))
			processes.append(process)
			startIndex = endIndex
			endIndex = endIndex + elementsForOneThread
	for proc in processes:
		proc.start()
	for proc in processes:
		proc.join()
	print("Fetched from MiST OK using ids")
	return (proteinIdToSeq.copy(), proteinIdToTrueId.copy())


def fetchFromMist(proteinIdToSeq, proteinIdToTrueId, multiProcList, proteinIdsToOrigNames=None):
	for proteinId in multiProcList:
		proteinId = proteinId.strip()
		for url in URLS:
		    preparedUrl = url + proteinId + ASEQ_SEQUENCE_FIELD
		    result = urllib.urlopen(preparedUrl).read()
		    proteinDict = json.loads(result)
		    if len(proteinDict):
		        proteinDict = proteinDict[0]
		        if "Aseq" in proteinDict and "sequence" in proteinDict["Aseq"]:
		            proteinSeq = proteinDict["Aseq"]["sequence"]
		            if not proteinIdsToOrigNames:
		                if "Component" in proteinDict and "definition" in proteinDict["Component"]:
		                    proteinName = proteinId + "_" + proteinDict["Component"]["definition"].split(",")[0]
		                    proteinIdToSeq[proteinName] = proteinSeq
		                    proteinIdToTrueId[proteinName]  = proteinId
		            else:
		                for proteinName in proteinIdsToOrigNames[proteinId]:
		                    proteinIdToSeq[proteinName] = proteinSeq
		        break
# ============== Fetch from MiST# ============== #

####===================================================#####
### Fetching sequences from NCBI using Tree leaves names ###

def fetchSequencesForTreeAndSave(treeObject):
	proteinIds1 = set()
	proteinIds2 = set()
	proteinIds03 = set()
	proteinIdsToSeq1 = None
	proteinIdsToSeq2 = None
	proteinIdsToSeq3 = None
	proteinIdsToOrigNames1 = collections.defaultdict(set)
	proteinIdsToOrigNames2 = collections.defaultdict(set)
	proteinIdsToOrigNames3 = collections.defaultdict(set)
	terminals = treeObject.get_leaves()
	for protein in terminals:
		originalProteinName = protein.name.strip("'")
		fullProteinName = REGEX_NUMBER_UNDERSCORE.sub("", originalProteinName)
		fullProteinNameSplitted = fullProteinName.split("_")

 		partProteinName = fullProteinNameSplitted[0].strip()
		proteinIds2.add(partProteinName)
		proteinIdsToOrigNames2[partProteinName].add(originalProteinName)

		if len(fullProteinNameSplitted) >= 2:
			partProteinName = "_".join(fullProteinNameSplitted[0:2]).strip()
			proteinIds1.add(partProteinName)
			proteinIdsToOrigNames1[partProteinName].add(originalProteinName)

			# this case can happen only for MiST Ids
			if len(fullProteinNameSplitted) >= 3:
				partProteinName = "_".join(fullProteinNameSplitted[0:3]).strip()
				proteinIds03.add(partProteinName)
				proteinIdsToOrigNames3[partProteinName].add(originalProteinName)

	# multiporcess dicts from usual sets to fetch from MiST
	proteinIdsForMist1 = manager.list(proteinIds1)
	proteinIdsForMist2 = manager.list(proteinIds2)
	proteinIdsForMist3 = manager.list(proteinIds03)
	if FETCH_FROM_NCBI:
		proteinIdsToSeq1 = fetchSequencesAndGetNameToSeqMap(proteinIds1, proteinIdsToOrigNames1)
		proteinIdsToSeq2 = fetchSequencesAndGetNameToSeqMap(proteinIds2, proteinIdsToOrigNames2)
	if FETCH_FROM_MIST:
		proteinIdsToSeq3ForMist = fetchFromMistByIds(proteinIdsForMist3, proteinIdsToOrigNames3)
		if proteinIdsToSeq3ForMist:
			proteinIdsToSeq3ForMist = proteinIdsToSeq3ForMist[0]
		if not FETCH_FROM_NCBI:
			proteinIdsToSeq1ForMist = fetchFromMistByIds(proteinIdsForMist1, proteinIdsToOrigNames1)[0]
			if proteinIdsToSeq1ForMist:
				proteinIdsToSeq1ForMist = proteinIdsToSeq1ForMist[0]
			proteinIdsToSeq2ForMist = fetchFromMistByIds(proteinIdsForMist2, proteinIdsToOrigNames2)[0]
			if proteinIdsToSeq2ForMist:
				proteinIdsToSeq2ForMist = proteinIdsToSeq2ForMist[0]
	with open(OUTPUT_FILE, "w") as outputFile:
		if FETCH_FROM_NCBI:
			saveFetchedSeqsForTree(proteinIdsToSeq1, outputFile)
			saveFetchedSeqsForTree(proteinIdsToSeq2, outputFile)
		if FETCH_FROM_MIST:
			saveFetchedSeqsForTree(proteinIdsToSeq3ForMist, outputFile)
			if not FETCH_FROM_NCBI:
				saveFetchedSeqsForTree(proteinIdsToSeq1ForMist, outputFile)
				saveFetchedSeqsForTree(proteinIdsToSeq2ForMist, outputFile)

def fetchSequencesAndGetNameToSeqMap(proteinIds, proteinIdsToOrigNames):
	proteinIdsSeqs = dict()
	proteinIdsHandle = None
	if not proteinIds:
		return None
	try:
		proteinIdsHandle = getHandleOfFetchedSequencesFromNcbi(proteinIds)
		if proteinIdsHandle:
			print("NCBI hanlde is present")
			for eachRecord in SeqIO.parse(proteinIdsHandle, "fasta"):
				for name in proteinIdsToOrigNames[eachRecord.id]:
					seq = str(eachRecord.seq)
					proteinIdsSeqs[name] = seq
	except Exception, e:
		print(e)
	finally:
		if proteinIdsHandle:
			proteinIdsHandle.close()
	return proteinIdsSeqs

def saveFetchedSeqsForTree(proteinIdsToSeq, outputFile):
	if proteinIdsToSeq and len(proteinIdsToSeq):
		for name, seqs in proteinIdsToSeq.items():
			outputFile.write(">" + name + "\n")
			outputFile.write(seqs + "\n")
####===================================================#####

# A generic fetcher. It returns a handle of fetched records
@timeout_decorator.timeout(ENTREZ_EFETCH_TIMEOUT, use_signals=True)
def getHandleOfFetchedSequencesFromNcbi(proteinIds):
	handle = None
	try:
		#handle = Entrez.efetch(db="protein", id="OYV75139.1,ACR67403.1", rettype="fasta", retmode="text")
		handle = Entrez.efetch(db="protein", id=",".join(proteinIds), rettype="fasta", retmode="text")
		print("Fetched from NCBI OK")
	except Exception, e:
		print ("Couldn't retrieve sequences by id from NCBI in time")
		if handle:
			handle.close()
	return handle

def isInOutOk(inputFile, outputFile, isTree=False):
	if not inputFile and not outputFile:
		return False
	if not inputFile or not outputFile:
		if isTree:
			print("Both input file name with the tree in newick format and output file name should be provided!")
		elif not FETCH_FROM_TREE:
			print("Both input file name with the sequenes and output file name should be provided!")
		return False
	if isTree:
		print("Changing tree leaves names")
	else:
		print("Changing sequences names")
	return True


def main(argv):
	initialize(argv)
	if FETCH_FROM_IDS and FETCH_FROM_TREE:
		print("You can't fetch both using id list and tree leaves names! Exiting")
		return
	if FETCH_FROM_IDS:
		fetchNamesAndSave()
	elif not FETCH_FROM_TREE:
		getChangedNamesForSeqsAndSave()
	treeObject = getChangedNamesForTree()
	if FETCH_FROM_TREE:
		fetchSequencesForTreeAndSave(treeObject)


if __name__ == "__main__":
	main(sys.argv)
