#!/usr/bin/python
#author: Vadim M. Gumerov; 09/08/2017

import sys, getopt, fileinput, os, traceback
import collections


'''Script cretaes COGs based on results of blast all vs all. Input: bunch of blast files in 6 outformat. 
'''
#usage: 

INPUT_DIR = "./blast_output"
OUTPUT_FILENAME = "COGs.txt"
DELIM="|"
COLUMN_NUM=1
IDENTITY_THRESHOLD=0
EVAL_THRESHOLD = 1e-90
COVERAGE_THRESHOLD = 90.0
DO_MERGE=True

#{g1:2, g2:10, ...}
GENOMENM_TO_NUM_OF_PROTS = dict()

#{ prot11:[(prot21, eval), (prot23, eval), ...], prot12:[(prot25, eval), (prot29, eval), ...] }
FIRST_GENOME_TO_SEC_GENOME = collections.defaultdict(list)

#{ Genome1ToGenome2:{prot11:[(prot21, eval), (prot23, eval), ...], prot12:[(prot25, eval), (prot29, eval), ...]}, Genome2ToGenome1: {...}, ... ], ...}  
GENOMENM_TO_PROT_TOPROT_LIST_MAP = dict()

PROT_TO_CLUSTER = dict()
PROT_TO_CLUSTERSET = collections.defaultdict(set)
CLUSTER_TO_PROTSET = collections.defaultdict(set)

GENOME_PAIRS = list()
GENOME_SET = set()

USAGE = sys.argv[0] + ' -i input directory -o output file name -d delimeter -c column number -t identity threshold -e E-value threashold -v coverage threshold -m merge clusters or not (yes|no)'
#DEFAULT_PARAMS = ["INPUT_DIR ", "OUTPUT_FILENAME ", "DELIM ", "COLUMN_NUM ", "IDENTITY_THRESHOLD ", "EVAL_THRESHOLD ", "COVERAGE_THRESHOLD ", "DO_MERGE "]
#DEFAULT_VALUES = [INPUT_DIR, OUTPUT_FILENAME, DELIM, COLUMN_NUM, IDENTITY_THRESHOLD, EVAL_THRESHOLD, COVERAGE_THRESHOLD, DO_MERGE]

def initialyze(argv):
	global INPUT_DIR, OUTPUT_FILENAME, DELIM, COLUMN_NUM, IDENTITY_THRESHOLD, EVAL_THRESHOLD, COVERAGE_THRESHOLD, DO_MERGE
	try:
		opts, args = getopt.getopt(argv[1:],"hi:o:d:c:t:e:v:m",["inputDir=", "outputFileName=", "delimeter=", "columnNumber=", "identity=", "eValue=", "coverage=", "merge="])
	except getopt.GetoptError:
		print USAGE + " Error"
		sys.exit(2)
	for opt, arg in opts:
		if opt == '-h':
			print USAGE
			sys.exit()
		elif opt in ("-i", "--inputDir"):
			INPUT_DIR = arg.strip()
		elif opt in ("-o", "--outputFileName"):
			OUTPUT_FILENAME = str(arg).strip()
		elif opt in ("-d", "--delimeter"):
			DELIM = arg
		elif opt in ("-c", "--columnNumber"):
			COLUMN_NUM = int(arg)
		elif opt in ("-t", "--identity"):
			IDENTITY_THRESHOLD = float(arg)
		elif opt in ("-e", "--eValue"):
			EVAL_THRESHOLD = float(arg)
		elif opt in ("-v", "--coverage"):
			COVERAGE_THRESHOLD = float(arg)
		elif opt in ("-m", "--merge"):
			if arg == "no":
				DO_MERGE = False
	

#process input data and generate sets of unique proteins with eval <= eval threshold for each input genome in comparison one vs one
def processInputData():
	os.chdir(INPUT_DIR)
	for inputFile in os.listdir(os.getcwd()):
		numOfProteinsInGenome = set()
		FIRST_GENOME_TO_SEC_GENOME = collections.defaultdict(list)
		try:
			for record in fileinput.input(inputFile):
				recordSpl = record.split("\t")
				firstProt = recordSpl[0]
				secondProt = recordSpl[1]
				firstProt = firstProt.split(DELIM)[COLUMN_NUM]
			
				GENOME_SET.add(firstProt[:3])
				numOfProteinsInGenome.add(firstProt)
				secondProt = secondProt.split(DELIM)[COLUMN_NUM]
					
				eVal = recordSpl[10]
				coverage = recordSpl[12]
				identity = recordSpl[2]
				
				if float(eVal) <= EVAL_THRESHOLD and float(coverage) >= COVERAGE_THRESHOLD and float(identity) >= IDENTITY_THRESHOLD:
					FIRST_GENOME_TO_SEC_GENOME[firstProt].append(secondProt)
	
			firstGenomeNm = firstProt[:3]
			secondGenomeNm = secondProt[:3]
			oneGenomeNmToAnotherGenomeNm = firstGenomeNm + "_vs_" + secondGenomeNm
			if not (secondGenomeNm + "_vs_" + firstGenomeNm) in GENOME_PAIRS:
				GENOME_PAIRS.append(oneGenomeNmToAnotherGenomeNm)
				
			#GENOMENM_TO_NUM_OF_PROTS[firstGenomeNm] = len(numOfProteinsInGenome)
			
			GENOMENM_TO_PROT_TOPROT_LIST_MAP[oneGenomeNmToAnotherGenomeNm] = FIRST_GENOME_TO_SEC_GENOME
		except Exception, e:
			print "Problem happened: ", traceback.print_exc()
		finally:
			fileinput.close()
		
	print "Number of compared genomes: ", str(len(GENOME_SET))




#Create COGs
def createCOGs():
	print "GENOME_PAIRS ", str(GENOME_PAIRS)
	clusterName = 0
	for everyGenomePair in GENOME_PAIRS:
		
		frstGenNmToSecGenNm = everyGenomePair
		secGenNmToFrstGenNm = everyGenomePair.split("_vs_")[1] + "_vs_" + everyGenomePair.split("_vs_")[0]

		
		FIRST_GENOME_TO_SEC_GENOME = GENOMENM_TO_PROT_TOPROT_LIST_MAP[frstGenNmToSecGenNm]
		secondGenomeToFirstGenome = GENOMENM_TO_PROT_TOPROT_LIST_MAP[secGenNmToFrstGenNm]


		for firstGenomeFirstProt in FIRST_GENOME_TO_SEC_GENOME:
			listOfProtsInSecGenome_ForProtInFrstGenome = FIRST_GENOME_TO_SEC_GENOME[firstGenomeFirstProt]
			
			for firstGenomeSecondProt in listOfProtsInSecGenome_ForProtInFrstGenome:
				if firstGenomeSecondProt in secondGenomeToFirstGenome:
					listOfProtsInFrstGenome_ForProtInSecGenome = secondGenomeToFirstGenome[firstGenomeSecondProt]
					
					for secondGenomeSecondProt in listOfProtsInFrstGenome_ForProtInSecGenome:						
						if secondGenomeSecondProt == firstGenomeFirstProt:
							if DO_MERGE:
								clusterName = processIfDoMerge(firstGenomeFirstProt, firstGenomeSecondProt, clusterName)
							else:
								clusterName = processIfNotMerge(firstGenomeFirstProt, firstGenomeSecondProt, clusterName)
										

def processIfNotMerge(firstGenomeFirstProt, firstGenomeSecondProt, clusterName):
	if firstGenomeFirstProt not in PROT_TO_CLUSTERSET:
		if firstGenomeSecondProt not in PROT_TO_CLUSTERSET:
			PROT_TO_CLUSTERSET[firstGenomeFirstProt].add(clusterName)										
			CLUSTER_TO_PROTSET[clusterName].add(firstGenomeFirstProt)
			PROT_TO_CLUSTERSET[firstGenomeSecondProt].add(clusterName)
			CLUSTER_TO_PROTSET[clusterName].add(firstGenomeSecondProt)
			clusterName+=1
		else:
			usedClusterNames = PROT_TO_CLUSTERSET[firstGenomeSecondProt]
			for clstName in usedClusterNames:
				PROT_TO_CLUSTERSET[firstGenomeFirstProt].add(clstName)
				CLUSTER_TO_PROTSET[clstName].add(firstGenomeFirstProt)		
	else:
		if firstGenomeSecondProt not in PROT_TO_CLUSTERSET:
			usedClusterNames = PROT_TO_CLUSTERSET[firstGenomeFirstProt]								
			for clstName in usedClusterNames:
				PROT_TO_CLUSTERSET[firstGenomeSecondProt].add(clstName)
				CLUSTER_TO_PROTSET[clstName].add(firstGenomeSecondProt)		
		else:
			usedClusterNames_ofFirstGenomeFirstProt = PROT_TO_CLUSTERSET[firstGenomeFirstProt]
			usedClusterNames_ofFirstGenomeSecondProt = PROT_TO_CLUSTERSET[firstGenomeSecondProt]
					
			PROT_TO_CLUSTERSET[firstGenomeFirstProt] = PROT_TO_CLUSTERSET[firstGenomeFirstProt].union(usedClusterNames_ofFirstGenomeSecondProt)
			PROT_TO_CLUSTERSET[firstGenomeSecondProt] = PROT_TO_CLUSTERSET[firstGenomeSecondProt].unoin(usedClusterNames_ofFirstGenomeFirstProt)
									
			for clstName1 in usedClusterNames_ofFirstGenomeFirstProt:
				CLUSTER_TO_PROTSET[clstName1].add(firstGenomeSecondProt)
									
			for clstName2 in usedClusterNames_ofFirstGenomeSecondProt:
				CLUSTER_TO_PROTSET[clstName2].add(firstGenomeFirstProt)
	return clusterName
	
	
def processIfDoMerge(firstGenomeFirstProt, firstGenomeSecondProt, clusterName):
	if firstGenomeFirstProt not in PROT_TO_CLUSTER:
		if firstGenomeSecondProt not in PROT_TO_CLUSTER:
			PROT_TO_CLUSTER[firstGenomeFirstProt] = clusterName
			CLUSTER_TO_PROTSET[clusterName].add(firstGenomeFirstProt)
			PROT_TO_CLUSTER[firstGenomeSecondProt] = clusterName
			CLUSTER_TO_PROTSET[clusterName].add(firstGenomeSecondProt)
			clusterName+=1
		else:
			usedClusterName = PROT_TO_CLUSTER[firstGenomeSecondProt]
			PROT_TO_CLUSTER[firstGenomeFirstProt] = usedClusterName
			CLUSTER_TO_PROTSET[usedClusterName].add(firstGenomeFirstProt)
			
			
	else:
		if firstGenomeSecondProt not in PROT_TO_CLUSTER:
			usedClusterName = PROT_TO_CLUSTER[firstGenomeFirstProt]
			PROT_TO_CLUSTER[firstGenomeSecondProt] = usedClusterName
			CLUSTER_TO_PROTSET[usedClusterName].add(firstGenomeSecondProt)
		else:
			usedClusterName = PROT_TO_CLUSTER[firstGenomeFirstProt]
			clusterNameToChange = PROT_TO_CLUSTER[firstGenomeSecondProt]
			if usedClusterName != clusterNameToChange:
				protsToChangeClstBelonging = CLUSTER_TO_PROTSET[clusterNameToChange]
				for prot in protsToChangeClstBelonging:
					PROT_TO_CLUSTER[prot] = usedClusterName
				CLUSTER_TO_PROTSET[usedClusterName] = CLUSTER_TO_PROTSET[usedClusterName].union(protsToChangeClstBelonging)
				del CLUSTER_TO_PROTSET[clusterNameToChange]
	return clusterName
		
def printData():
	os.chdir("..")
	with open(OUTPUT_FILENAME, "w") as outFile:
		sortedProts = sorted(CLUSTER_TO_PROTSET.values(), key=len)
		clstName = 1
		for proteins in sortedProts:
			outFile.write(str(clstName) + ":" + "\n")
			clstName+=1
			for prot in proteins:
				outFile.write(prot[4:] + "\n")


def main(argv):
	initialyze(argv)
	processInputData()
	createCOGs()
	printData()
		
if __name__ == "__main__":
	main(sys.argv)
	
