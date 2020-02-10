#!/usr/bin/python
import sys, getopt
import re
import urllib
import json
import random
from ete2 import Tree
import math
import multiprocessing


USAGE = "\n\nThe script cluster genes in gene neighborhoods json file based on the shared domains.\n\n" + \
	"It also determines operons. \n\n" + \
	"python 	" + sys.argv[0] + '''
	-h || --help               - help
	-i || --itree              - input file with phylogenetic tree in newick format
	-s || --ilist              - input file with a list of ids
 	[-n || --tolerance]        - NOT_SHARED_DOMAIN_NUMBER_TOLERANCE parameter
	[-f || --first_color]      - first color threshold; default is 100
	[-s || --second_color]     - first color threshold; default is 1000
	[-r || --random_seed]      - random color generator seed
	[-p || --operon_tolerance] - allowed distance between genes in operons, default is 150
	[-c || --proc_num]         - number of processes to run simultaneously, default is 50. The number is big beacous the process is not CPU-intencive
	-o || --ofile              - output file with sequences with changed names
	'''

manager = multiprocessing.Manager()
NUMBER_OF_PROCESSES = 50
OPERON_COLOR_DICT = manager.list(["#ff4d4d", "#1a75ff", "#cc33ff", "#00cc00", "#ff9900", "#993333", "#000099", "#00b3b3", "#660033", "#00b386", \
"#663399", "#B22222", "#9ACD32", "#0000FF", "#D2691E", "#778899", "#2F4F4F", "#FFD700", "#FF1493", "#00FA9A", "#4682B4", "#00BFFF", \
"#7FFFD4", "#191970", "#993399", "#CCCC66", "#FF9933", "#6699FF", "#99FF33", "#66FFFF", "#99CCFF" ])
SELECTED_COLORS = set()
NUMBER_OF_GENERATED_COLORS = 0
FIRST_COLOR_THRESHOLD = 100
SECOND_COLOR_THRESHOLD = 1000
#random seed for color generation
RANDOM_SEED = 2
RANDOM_STATE = random.getstate()



INPUT_FILE = None
TREE_FILE = None
NOT_SHARED_DOMAIN_NUMBER_TOLERANCE = 0
OPERON_TOLERANCE = 200
NUM_OF_NEIGHBORS = 5
OUTPUT_FILE = None

BASE_URL = "https://api.mistdb.caltech.edu/v1/genes"
GENE_FIELDS = "id,stable_id,version,locus,strand,start,stop,length,pseudo,product"
PFAM = "pfam31"


REGEX_NUMBER_UNDERSCORE = re.compile(r"^\d+_")
PROCESSED_STABLE_IDS = set()
PROCESSED_MAIN_GENE_STABLE_IDS = set()
GENE_TO_NEIGHBORS = manager.dict()

def initialize(argv):
	global INPUT_FILE, TREE_FILE, NOT_SHARED_DOMAIN_NUMBER_TOLERANCE, OPERON_TOLERANCE, OUTPUT_FILE
	global FIRST_COLOR_THRESHOLD, SECOND_COLOR_THRESHOLD, RANDOM_SEED, RANDOM_STATE, NUMBER_OF_PROCESSES, NUM_OF_NEIGHBORS
	try:
		opts, args = getopt.getopt(argv[1:],"hi:s:n:f:s:r:p:b:c:o:",["help", "itree=", "ilist=", "tolerance=", "first_color=", "second_color=", "random_seed=", "operon_tolerance=", "num_ofneighbors=", "proc_num=", "ofile="])
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
			elif opt in ("-i", "--itree"):
				TREE_FILE = str(arg).strip()
			elif opt in ("-n", "--tolerance"):
				NOT_SHARED_DOMAIN_NUMBER_TOLERANCE = int(arg)
			elif opt in ("-s", "--ilist"):
				INPUT_FILE = str(arg).strip()
			elif opt in ("-f", "--first_color"):
				FIRST_COLOR_THRESHOLD = int(arg)
			elif opt in ("-s", "--second_color"):
				SECOND_COLOR_THRESHOLD = int(arg)
			elif opt in ("-r", "--random_seed"):
				RANDOM_SEED = int(arg)
			elif opt in ("-p", "--operon_tolerance"):
				OPERON_TOLERANCE = int(arg)
			elif opt in ("-b", "--num_ofneighbors"):
				neighbors = int(arg)
				if (neighbors >= 5 and neighbors <= 15):
					NUM_OF_NEIGHBORS = neighbors
				elif (neighbors > 15):
					NUM_OF_NEIGHBORS = 15
			elif opt in ("-c", "--proc_num"):
				NUMBER_OF_PROCESSES = int(arg)
			elif opt in ("-o", "--ofile"):
				OUTPUT_FILE = str(arg).strip()
	except Exception as e:
		print "===========ERROR==========\n " + str(e) + USAGE
		sys.exit(2)

	random.seed(RANDOM_SEED)
	RANDOM_STATE = random.getstate()

def findGene(geneNameOrId):
	params = urllib.urlencode({'search': geneNameOrId, 'fields': GENE_FIELDS, 'fields.Aseq': PFAM})
	result = urllib.urlopen(BASE_URL + "?%s" % params)
	try:
		result = json.loads(result.read())
	except Exception as e:
		result = None
		print("Exception happened whyle trying to decode json: " + str(e))
		print("So returning None")
	finally:
		return result

def getGeneAndProcessGeneNeighbors(geneStableIdList):
	duplicateCounter = 1
	for geneIndex in xrange(len(geneStableIdList[0])):
		gene = geneStableIdList[0][geneIndex]
		mainGeneDict = findGene(gene)
		fullProteinName = geneStableIdList[3][geneIndex]
		if mainGeneDict and len(mainGeneDict):
			duplicateCounter = getGeneNeighborsAndPrepareDomains(mainGeneDict[0], duplicateCounter, fullProteinName)
		else:
			gene = geneStableIdList[1][geneIndex]
			mainGeneDict = findGene(gene)
			if mainGeneDict and len(mainGeneDict):
				duplicateCounter = getGeneNeighborsAndPrepareDomains(mainGeneDict[0], duplicateCounter, fullProteinName)
			else:
				gene = geneStableIdList[2][geneIndex]
				mainGeneDict = findGene(gene)
				if mainGeneDict and len(mainGeneDict):
					duplicateCounter = getGeneNeighborsAndPrepareDomains(mainGeneDict[0], duplicateCounter, fullProteinName)

def getGeneNeighborsAndPrepareDomains(mainGeneDict, duplicateCounter, fullProteinName):
	if mainGeneDict['Aseq'] and mainGeneDict['Aseq']['pfam31'] and len(mainGeneDict['Aseq']['pfam31']):
		mainGeneSortedDomains = sorted(mainGeneDict['Aseq']['pfam31'], key=lambda x: x['ali_from'], reverse=False)
		mainGeneDomainsWithNoOverlaps, mainGeneDomainWithNoOverlapsNamesOnly = removeOverlapps(mainGeneSortedDomains)
		mainGeneDict['Aseq']['pfam31'] = mainGeneDomainsWithNoOverlaps
		mainGeneDict['Aseq']['pfam31NamesOnly'] = mainGeneDomainWithNoOverlapsNamesOnly
	params = urllib.urlencode({'fields': GENE_FIELDS, 'fields.Aseq': PFAM, 'amount': NUM_OF_NEIGHBORS})
	result = urllib.urlopen(BASE_URL + "/" + mainGeneDict["stable_id"] + "/neighbors?%s" % params)
	genesList = json.loads(result.read())

	previousGene = None
	wasStrandChanegd = False
	middleGeneProcessed = False
	operonCounter = 0
	usedAsColourIndex = False
	for eachGene in genesList:
		if eachGene['Aseq'] and eachGene['Aseq']['pfam31'] and len(eachGene['Aseq']['pfam31']):
			sortedDomains = sorted(eachGene['Aseq']['pfam31'], key=lambda x: x['ali_from'], reverse=False)
			domainsWithNoOverlaps, domainWithNoOverlapsNamesOnly = removeOverlapps(sortedDomains)
			eachGene['Aseq']['pfam31'] = domainsWithNoOverlaps
			eachGene['Aseq']['pfam31NamesOnly'] = domainWithNoOverlapsNamesOnly
		if int(eachGene["id"]) < int(mainGeneDict["id"]):
			if previousGene:
				operonCounter, usedAsColourIndex = identifyIfBelongToOperon(previousGene, eachGene, operonCounter, usedAsColourIndex)
		else:
			if not middleGeneProcessed:
				operonCounter, usedAsColourIndex = identifyIfBelongToOperon(previousGene, mainGeneDict, operonCounter, usedAsColourIndex)
				operonCounter, usedAsColourIndex = identifyIfBelongToOperon(mainGeneDict, eachGene, operonCounter, usedAsColourIndex)
				middleGeneProcessed = True
			else:
				operonCounter, usedAsColourIndex = identifyIfBelongToOperon(previousGene, eachGene, operonCounter, usedAsColourIndex)
		previousGene = eachGene

	genesList.append(mainGeneDict)
	if mainGeneDict["stable_id"] not in GENE_TO_NEIGHBORS:
		GENE_TO_NEIGHBORS[fullProteinName] = genesList
	else:
		GENE_TO_NEIGHBORS[fullProteinName + "@" + str(duplicateCounter)] = genesList
		duplicateCounter+=1

	return duplicateCounter

def identifyIfBelongToOperon(previousGene, currentGene, operonCounter, usedAsColourIndex):
	if previousGene and currentGene:
		if previousGene["strand"] == currentGene["strand"]:
		    distance = currentGene["start"] - previousGene["stop"]
		    if distance > 0:
		        if distance <= OPERON_TOLERANCE:
		            previousGene["operon"] = operonCounter
		            currentGene["operon"] = operonCounter
		            previousGene["operonColour"] = OPERON_COLOR_DICT[operonCounter]
		            currentGene["operonColour"] = OPERON_COLOR_DICT[operonCounter]
		            usedAsColourIndex = True
		        else:
					if usedAsColourIndex:
						operonCounter+=1
						usedAsColourIndex = False
		    else:
				previousGene["operon"] = operonCounter
				currentGene["operon"] = operonCounter
				previousGene["operonColour"] = OPERON_COLOR_DICT[operonCounter]
				currentGene["operonColour"] = OPERON_COLOR_DICT[operonCounter]
				usedAsColourIndex = True
		else:
			if usedAsColourIndex:
				operonCounter+=1
				usedAsColourIndex = False
	return (operonCounter, usedAsColourIndex)

def clusterGenesBasedOnSharedDomains(gene_to_neighbors):
	gene1ClusterId = None
	mainGene1ClusterId = 0
	gene1ClusterColor = None
	for mainGene1StableId, allNeighborGenes1 in gene_to_neighbors.items():
		mainGene1ClusterId+=1
		for gene1Index in xrange(len(allNeighborGenes1)):
			gene1 = allNeighborGenes1[gene1Index]
			if gene1['stable_id'] not in PROCESSED_STABLE_IDS:
				if  gene1['Aseq'] and 'pfam31NamesOnly' in gene1['Aseq']:
					numberOfDomainsInGene1 = len(set(gene1['Aseq']['pfam31NamesOnly']))
					numberOfMaxDomains = numberOfDomainsInGene1
					if 'clusterId' in gene1 and gene1['clusterId']:
						gene1ClusterId = gene1['clusterId']
						gene1ClusterColor = gene1['clusterColor']
					else:
						gene1ClusterId = None
						gene1ClusterColor = None
					for gene2 in allNeighborGenes1:
						# check first wether gene2 was already clustered
						if gene2['stable_id'] not in PROCESSED_STABLE_IDS:
							if gene2['Aseq'] and gene1['stable_id'] != gene2['stable_id']:
								if not 'clusterId' in gene2 and 'pfam31NamesOnly' in gene2['Aseq']:
									numberOfSharedDomains = len(set(gene1['Aseq']['pfam31NamesOnly']) & set(gene2['Aseq']['pfam31NamesOnly']))
									numberOfMaxDomains = getNumberOfMaxDomains(gene2, numberOfDomainsInGene1)
									if numberOfSharedDomains > 0 and abs(numberOfMaxDomains - numberOfSharedDomains) <= NOT_SHARED_DOMAIN_NUMBER_TOLERANCE:
										if gene1ClusterId:
											gene2['clusterId'] = gene1ClusterId
											gene2['clusterColor'] = gene1ClusterColor
										else:
											gene1ClusterId = str(mainGene1ClusterId) + str(gene1Index)
											gene1ClusterColor = getNextColour()
											gene1['clusterId'] = gene1ClusterId
											gene2['clusterId'] = gene1ClusterId
											gene1['clusterColor'] = gene1ClusterColor
											gene2['clusterColor'] = gene1ClusterColor

					for mainGene2StableId, allNeighborGenes2 in gene_to_neighbors.items():
						# first check that these are two different gene neigborhoods
						# and whether mainGene2StableId was already processed
						if mainGene1StableId != mainGene2StableId and mainGene2StableId not in PROCESSED_MAIN_GENE_STABLE_IDS:
							for gene3 in allNeighborGenes2:
								# check wether gene3 was already clustered
								if gene3['stable_id'] not in PROCESSED_STABLE_IDS:
									#we shouldn't check if gene3 is different than gene1 because this is another gene set
									if gene3['Aseq']:
										if not 'clusterId' in gene3 and 'pfam31NamesOnly' in gene3['Aseq']:
											numberOfSharedDomains = len(set(gene1['Aseq']['pfam31NamesOnly']) & set(gene3['Aseq']['pfam31NamesOnly']))
											numberOfMaxDomains = getNumberOfMaxDomains(gene3, numberOfDomainsInGene1)
											if numberOfSharedDomains > 0 and abs(numberOfMaxDomains - numberOfSharedDomains) <= NOT_SHARED_DOMAIN_NUMBER_TOLERANCE:
												if gene1ClusterId:
													gene3['clusterId'] = gene1ClusterId
													gene3['clusterColor'] = gene1ClusterColor
												else:
													gene1ClusterId = str(mainGene1ClusterId) + str(gene1Index)
													gene1ClusterColor = getNextColour()
													gene1['clusterId'] = gene1ClusterId
													gene3['clusterId'] = gene1ClusterId
													gene1['clusterColor'] = gene1ClusterColor
													gene3['clusterColor'] = gene1ClusterColor

			PROCESSED_STABLE_IDS.add(gene1['stable_id'])
		PROCESSED_MAIN_GENE_STABLE_IDS.add(mainGene1StableId)

def removeOverlapps(domainsSorted):
	if (len(domainsSorted)) > 0:
		tolerance = 10
		pfam31Final = []
		pfam31FinalNamesOnly = list()
		pfam1 = domainsSorted[0]
		significantPfam = pfam1
		overlapLength = None
		lastAdded = pfam1
		pfam31Final.append(pfam1)
		pfam31FinalNamesOnly.append(pfam1['name'])
		for pfam2 in domainsSorted[1:]:
			if pfam1['ali_to'] > pfam2['ali_from']:
				overlapLength = pfam1['ali_to'] - pfam2['ali_from']
				if overlapLength > tolerance:
					significantPfam = compareEvalues(pfam1, pfam2)
					# if the previously added is pfam1 and it's less significant than pfam 2
					# then remove this previously added and add pfam 2
					if lastAdded == pfam1 and lastAdded != significantPfam:
						pfam31Final.remove(lastAdded)
						pfam31Final.append(significantPfam)
						pfam31FinalNamesOnly.remove(lastAdded['name'])
						pfam31FinalNamesOnly.append(significantPfam['name'])
					lastAdded = significantPfam
				else:
					pfam31Final.append(pfam2)
					pfam31FinalNamesOnly.append(pfam2['name'])
					lastAdded = pfam2
					significantPfam = pfam2
				pfam1 = significantPfam
			else:
				pfam31Final.append(pfam2)
				pfam31FinalNamesOnly.append(pfam2['name'])
				lastAdded = pfam2
				significantPfam = pfam2
				pfam1 = significantPfam
		return (pfam31Final, pfam31FinalNamesOnly)
	return (None, None)

def compareEvalues(pfam1, pfam2):
	eval1 = pfam1['i_evalue']
	eval2 = pfam2['i_evalue']
	significantPfam = None
	if eval1 < eval2:
		significantPfam = pfam1
	elif eval1 > eval2:
		significantPfam = pfam2
	elif eval1 == eval2:
		if (pfam1['ali_to'] - pfam1['ali_from']) >= (pfam2['ali_to'] - pfam2['ali_from']):
			significantPfam = pfam1
		else:
			significantPfam = pfam2
	return significantPfam

def getNumberOfMaxDomains(gene, numberOfDomainsInGene1):
	numberOfDomainsInThisGene = len(set(gene['Aseq']['pfam31NamesOnly']))
	if numberOfDomainsInGene1 < numberOfDomainsInThisGene:
		return numberOfDomainsInThisGene
	return numberOfDomainsInGene1

#get next predictabel color (random seed was set at the beginnig of the file)
def getNextColour():
	global NUMBER_OF_GENERATED_COLORS
	global RANDOM_STATE
	random.setstate(RANDOM_STATE)
	h = random.random()

	if NUMBER_OF_GENERATED_COLORS <= FIRST_COLOR_THRESHOLD:
		s = random.random()
		v = 0.95
	elif NUMBER_OF_GENERATED_COLORS > FIRST_COLOR_THRESHOLD and NUMBER_OF_GENERATED_COLORS <= SECOND_COLOR_THRESHOLD:
		s = random.random()
		v = 0.95
	else:
		s = random.random()
		v = random.random()

	NUMBER_OF_GENERATED_COLORS+=1
	newColor = hsv_to_rgb(h, s, v)
	colourCounter = 0
	while newColor in SELECTED_COLORS:
		h = random.random()
		s = random.random()
		newColor = hsv_to_rgb(h, s, v)
		colourCounter+=1
		if colourCounter == 2:
			v = random.random()
			newColor = hsv_to_rgb(h, s, v)
			colourCounter = 0
			print "Colour counter exhausted!"

	SELECTED_COLORS.add(newColor)

	RANDOM_STATE = random.getstate()
	return newColor

#convert hsv to rgb
def hsv_to_rgb(h, s, v):
	# use golden ratio
	golden_ratio_conjugate = 0.618033988749895
	h += golden_ratio_conjugate
	h %= 1
	h_i = int(h*6)
	f = h*6 - h_i
	p = v * (1 - s)
	q = v * (1 - f*s)
	t = v * (1 - (1 - f) * s)
	if h_i == 0:
		r, g, b = v, t, p
	elif h_i == 1:
		r, g, b = q, v, p
	elif h_i == 2:
		r, g, b = p, v, t
	elif h_i == 3:
		r, g, b = p, q, v
	elif h_i == 4:
		r, g, b = t, p, v
	elif h_i == 5:
		r, g, b = v, p, q
	return "rgb" + str((int(r*256), int(g*256), int(b*256)))


def readTreeAndStartProcess():
	proteinIds1 = manager.list()
	proteinIds2 = manager.list()
	proteinIds3 = manager.list()
	originalNames = manager.list()
	with open(TREE_FILE, "r") as inputFile:
		treeObject = Tree(inputFile.read())

	terminals = treeObject.get_leaves()
	for protein in terminals:
		originalProteinName = protein.name.strip("'")
		fullProteinName = REGEX_NUMBER_UNDERSCORE.sub("", originalProteinName)
		partProteinName = "_".join(fullProteinName.split("_")[0:3]).strip()
		proteinIds1.append(partProteinName)
		partProteinName = "_".join(fullProteinName.split("_")[0:2]).strip()
		proteinIds2.append(partProteinName)
 		partProteinName = fullProteinName.split("_")[0].strip()
		proteinIds3.append(partProteinName)
		originalNames.append(originalProteinName)

	elementsForOneThread = int(math.ceil(len(proteinIds1)/float(NUMBER_OF_PROCESSES)))
	processes = list()
	startIndex = 0
	endIndex = elementsForOneThread
	for ind in xrange(NUMBER_OF_PROCESSES):
		if startIndex <= len(proteinIds1):
			process = multiprocessing.Process(target=getGeneAndProcessGeneNeighbors, \
				args=([proteinIds1[startIndex:endIndex], proteinIds2[startIndex:endIndex], proteinIds3[startIndex:endIndex], originalNames[startIndex:endIndex]],))
			processes.append(process)
			startIndex = endIndex
			endIndex = endIndex + elementsForOneThread

	for proc in processes:
		proc.start()

	for proc in processes:
		proc.join()
	#getGeneAndProcessGeneNeighbors([proteinIds1, proteinIds2, proteinIds3, originalNames])


def main(argv):
	initialize(argv)
	#listOfIds = []
	#~ with open(INPUT_FILE, "r") as inputFile:
		#~ for line in inputFile:
			#~ listOfIds.append(line.strip())
	readTreeAndStartProcess()
	# shallow copying DictProxy object created by Manager.dict() to standart python dict to continue processing
	gene_to_neighbors = GENE_TO_NEIGHBORS.copy()
	clusterGenesBasedOnSharedDomains(gene_to_neighbors)
	with open(OUTPUT_FILE, "w") as outputFile:
		json.dump(gene_to_neighbors, outputFile, default=lambda o: o.__dict__, sort_keys=True, indent=4)


if __name__ == "__main__":
	main(sys.argv)
