#!/usr/bin/python
import sys, getopt, json, collections, re
from Bio import Phylo, SeqIO
from ete2 import Tree, SeqMotifFace, TreeStyle, add_face_to_node

USAGE = "\nThis script enumerates protein sequence names on the provided phylogenetic tree and in the file with aligned proteins\n" + \
"and creates phylogenetic tree in svg format with protein domains after branches.\n" + \
"It additionally saves phylogenetic tree with changed protein names in newick format\n\n" + "python" + sys.argv[0] + '''
-i || --isequence     -input filew with sequences
[-s || --ialigned]    -input file with aligned sequences
-d || --ithird        -input file with phylogenetic tree  
-f || --ifourth       -input file with protein domains information
[-o || --oaligned]    -output file with aligned sequences with changed protein names
[-n || --osecond]     -output file with prepared tree with domains and changed protein names in svg format
[-b || --othird]      -output file with prepared tree with changed protein and no domains in newick format
'''

# Specified via program arguments
SEQS = None                                 #file with sequences
SEQS_ALIGNED = None          	            #file with aligned sequences
TREE_FILE = None    			            #file with tree
DOMAINS = None                              #file with protein domains information
OUTPUT_ALIGNED_FILENAME = "alignerProteins.fa"              #file with aligned sequences with changed protein names
OUTPUT_TREE_SVG_FILENAME = "newTree.svg"             #file with prepared tree with domains in svg format
OUTPUT_TREE_NEWICK_FILENAME = "newTree.newick"          #file with prepared tree with no domains in newick format


# Datastructures which are get initialized and manipulated by the program
ALIGNED_PROTEIN_NAME_TO_SEQ = dict()
PROTEIN_NAME_TO_SEQ = dict()
PROTEIN_DOMAINS = dict()
PROTEIN_TO_DOMAINS = collections.defaultdict(list) #{protName1: [[], [], ...], protName2: [[], [], ...]}


FEATURE_NAMES_TO_PROCESSED = dict()
ALIGNED_NAMES_TO_PROCESSED = dict()
PROTEIN_NAMES_TO_PROCESSED = dict()

PROCESSED_TO_FEATURE_NAMES = dict()
PROCESSED_TO_ALIGNED_NAMES = dict()
PROCESSED_TO_PROTEIN_NAMES = dict()

# Maximum number of domains in then merged domain syt to be displayed
MAX_DOMAIN_COUNT = 6
# Allowed overlap between adjacent domains. If the overlap precentage is bigger than this value the domains will be merged together 
ALLOWED_OVERLAP = 0.2
# seq.start, seq.end, shape, width, height, fgcolor, bgcolor
THIN_LINE = [0, 9, "[]", 0.2, 0.7, "black", "gray", ""]
INVISIBLE_LINE = [0, 9, "[]", 0.1, 0.7, "white", "white", ""]

# typical domain visualization construct: [10, 100, "[]", None, 10, "black", "#E8E8E8", "arial|2|black|Domain"]

TM_COLOR="#8c8c8c"
LCR_COLOR="#c71585"
DOMAIN_COLOR="white"
DOMAIN_BORDER_COLOR="steelblue"

def initialyze(argv):
	global SEQS, SEQS_ALIGNED, TREE_FILE, DOMAINS, OUTPUT_ALIGNED_FILENAME, OUTPUT_TREE_SVG_FILENAME, OUTPUT_TREE_NEWICK_FILENAME
	try:
		opts, args = getopt.getopt(argv[1:],"hi:s:d:f:o:n:b:",["isequence=", "ialigned=", "ithird=", "ifourth=", "oaligned=", "osecond=", "othird="])
		if len(opts) == 0:
			raise getopt.GetoptError("Options are required\n")
	except getopt.GetoptError as e:
		print "===========ERROR==========\n " + str(e) + USAGE
		sys.exit(2)
	for opt, arg in opts:
		if opt == '-h':
			print USAGE
			sys.exit()
		elif opt in ("-i", "--isequence"):
			SEQS = str(arg).strip()
		elif opt in ("-s", "--ialigned"):
			SEQS_ALIGNED = str(arg).strip()
		elif opt in ("-d", "--ithird"):
			TREE_FILE = str(arg).strip()
		elif opt in ("-f", "--ifourth"):
			DOMAINS = str(arg).strip()
		elif opt in ("-o", "--oaligned"):
			OUTPUT_ALIGNED_FILENAME = str(arg).strip()
		elif opt in ("-n", "--osecond"):
			OUTPUT_TREE_SVG_FILENAME = str(arg).strip()   
		elif opt in ("-b", "--othird"):
			OUTPUT_TREE_NEWICK_FILENAME = str(arg).strip()

def prepareProteinToDomainsDict():
	for proteinName, proteinData in PROTEIN_DOMAINS.items():
		if proteinData["domains"] or "tmRegions" in proteinData["tmInfo"] or proteinData["lowComplexity"]:
			tms = list()
			lowComplexityRegions = list()
			domains = list()
			domainsFinal = list()
			domainToCoords = dict()
			aliEnds = set()
			aliStarts = set()
			startsToDomainList = collections.defaultdict(list)
			
			domToMergeGrpNames = collections.defaultdict(set)
			domToMergeGrpStarts = collections.defaultdict(set)
			domToMergeGrpEnds = collections.defaultdict(set)
			domainsOK = dict()
			
			if "tmRegions" in proteinData["tmInfo"]:
				for region in proteinData["tmInfo"]["tmRegions"]:
					correctedCoords = addToCoordinateLists(aliStarts, aliEnds, int(region["tmSart"])-1, int(region["tmEnd"])-1)
					tms.append([correctedCoords[0], correctedCoords[1], "[]", None, 35, TM_COLOR, TM_COLOR, ""])
					startsToDomainList[correctedCoords[0]].append(correctedCoords[1])
				domains.extend(tms)
				
			for region in proteinData["lowComplexity"]:
				correctedCoords = addToCoordinateLists(aliStarts, aliEnds, int(region["start"])-1, int(region["end"])-1)
				lowComplexityRegions.append([correctedCoords[0], correctedCoords[1], "[]", None, 35, LCR_COLOR, LCR_COLOR, ""])
				startsToDomainList[correctedCoords[0]].append(correctedCoords[1])
			domains.extend(lowComplexityRegions)
				
			# Doing the following inorder to process domains in asscending order of their starts 	
			dmainStartsProcessed = []
			dmainStartToDomainInfo = {}
			for region in proteinData["domains"]:
				if region["aliStart"]-1 not in dmainStartsProcessed:
					dmainStartToDomainInfo[region["aliStart"]-1] = region
				# taking care of possible domains with the same start but different names
				else:
					region["aliStart"] = region["aliStart"]+1 
					dmainStartToDomainInfo[region["aliStart"]-1] = region					
				dmainStartsProcessed.append(region["aliStart"]-1)	

			# Here I'm sorting the starts in processinf them in the sorted order
			for key in sorted(dmainStartToDomainInfo):
				region = dmainStartToDomainInfo[key]
				correctedCoords = addToCoordinateLists(aliStarts, aliEnds, region["aliStart"]-1, region["aliEnd"]-1)
				aliStart = correctedCoords[0]
				aliEnd = correctedCoords[1]
				# taking care of domains with the same names but different coordinates
				domainName = str(region["domainName"])+"&"+str(aliStart)+str(aliEnd)
				domainSpan = correctedCoords[2]
				createMergeGrps(aliStart, aliEnd, domainName, domainSpan, domainToCoords, domToMergeGrpNames, domToMergeGrpStarts, domToMergeGrpEnds, domainsOK)							
				if domainName not in domToMergeGrpNames:
					domainsOK[domainName] = ""
				domainToCoords[domainName] = [aliStart, aliEnd, domainSpan]

			processSeparateDomains(domainsOK, domainToCoords, domains, startsToDomainList)
			processDomainsForMerge(domToMergeGrpNames, domToMergeGrpStarts, domToMergeGrpEnds, domains, startsToDomainList)
			domainsFinal.extend(domains)
			addThinLines(proteinName, domainsFinal, startsToDomainList)
			
			# If len of a protein is bigger than the last coordinate do the following:
			endOfLastDomain = max(aliEnds)
			startOfFirstDomain = min(aliStarts)
			if len(PROTEIN_NAME_TO_SEQ[proteinName]) > endOfLastDomain:
				thisThinLine = makeThinLine(endOfLastDomain+1, len(PROTEIN_NAME_TO_SEQ[proteinName])-1)
				domainsFinal.append(thisThinLine)
			# If start of the first domain is bigger then 0:
			if startOfFirstDomain > 0:
				thisThinLine = makeThinLine(0, startOfFirstDomain-1)
				domainsFinal.append(thisThinLine)
			
			PROTEIN_TO_DOMAINS[proteinName].extend(domainsFinal)

def addToCoordinateLists(starts, ends, regionStart, regionEnd):
	start = regionStart
	end = regionEnd
	if regionStart not in starts:
		starts.add(start)
	else:
		start = regionStart + 1
		starts.add(start)
	if regionEnd not in ends:
		ends.add(end)
	else:
		end = regionEnd + 1
		ends.add(end)
	return (start, end, end - start)
	
def createMergeGrps(aliStart, aliEnd, domainName, domainSpan, domainToCoords, domToMergeGrpNames, domToMergeGrpStarts, domToMergeGrpEnds, domainsOK):
	for processedDomain, domainCoords in domainToCoords.items():
		if aliStart < domainCoords[1]:
			overlapLen = aliStart - domainCoords[1]
			if overlapLen < 0 and (abs(overlapLen/float(domainSpan)) > ALLOWED_OVERLAP or abs(overlapLen/float(domainCoords[2])) > ALLOWED_OVERLAP):
				if processedDomain not in domToMergeGrpNames and domainName not in domToMergeGrpNames:
					domToMergeGrpNames[processedDomain] = set((domainName, processedDomain))
					domToMergeGrpNames[domainName] = domToMergeGrpNames[processedDomain]
					domToMergeGrpStarts[processedDomain] = set((aliStart, domainCoords[0]))
					domToMergeGrpStarts[domainName] = domToMergeGrpStarts[processedDomain]	
					domToMergeGrpEnds[processedDomain] = set((aliEnd, domainCoords[1]))
					domToMergeGrpEnds[domainName] = domToMergeGrpEnds[processedDomain]
				elif processedDomain in domToMergeGrpNames and domainName in domToMergeGrpNames:
					domToMergeGrpNames[processedDomain].add(domainName)
					domToMergeGrpStarts[processedDomain].add(aliStart)
					domToMergeGrpEnds[processedDomain].add(aliEnd)
				elif processedDomain in domToMergeGrpNames:
					mergeIfOneIsInMergeGrp(processedDomain, domainName, aliStart, aliEnd, domToMergeGrpNames, domToMergeGrpStarts, domToMergeGrpEnds)
				elif domainName in domToMergeGrpNames:
					mergeIfOneIsInMergeGrp(domainName, processedDomain, aliStart, aliEnd, domToMergeGrpNames, domToMergeGrpStarts, domToMergeGrpEnds)
				if processedDomain in domainsOK:
					del domainsOK[processedDomain]
					
def mergeIfOneIsInMergeGrp(domainName1, domainName2, aliStart, aliEnd, domToMergeGrpNames, domToMergeGrpStarts, domToMergeGrpEnds):
	domToMergeGrpNames[domainName1].add(domainName2)
	domToMergeGrpNames[domainName2] = domToMergeGrpNames[domainName1]
	domToMergeGrpStarts[domainName1].add(aliStart)
	domToMergeGrpStarts[domainName2] = domToMergeGrpStarts[domainName1]
	domToMergeGrpEnds[domainName1].add(aliEnd)
	domToMergeGrpEnds[domainName2] = domToMergeGrpEnds[domainName1]
	
def processSeparateDomains(domainsOK, domainToCoords, domains, startsToDomainList):
	for domainName in domainsOK:
		domains.append([domainToCoords[domainName][0], domainToCoords[domainName][1], "o", None, 35, DOMAIN_BORDER_COLOR, DOMAIN_COLOR, "arial|8|#008B8B|" + domainName.split("&")[0]])
		startsToDomainList[domainToCoords[domainName][0]].append(domainToCoords[domainName][1])
        	
def processDomainsForMerge(domToMergeGrpNames, domToMergeGrpStarts, domToMergeGrpEnds, domains, startsToDomainList):
	processedDomains = set()	
	for domain, mergeGroup in domToMergeGrpNames.items():
		if domain in processedDomains:
			continue
		aliStart = min(domToMergeGrpStarts[domain])
		aliEnd = max(domToMergeGrpEnds[domain])
		processedDomains.update(mergeGroup)
		domainName = "arial|8|#008B8B|"
		mergeDomainInd = 1
		# Sorting to show the domains in consistent ordere across proteins with the same merged domains
		for mergeDomain in sorted(mergeGroup)[:MAX_DOMAIN_COUNT]:
			mergeDomain = mergeDomain.split("&")[0]
			if mergeDomainInd%2 == 0:
				domainName = domainName + mergeDomain + "\n "
			else:
				domainName = domainName + mergeDomain + ", "
			mergeDomainInd+=1
		domains.append([aliStart, aliEnd, "()", None, 35, DOMAIN_BORDER_COLOR, DOMAIN_COLOR, domainName.rstrip(", |\n")])
		startsToDomainList[aliStart].append(aliEnd)
		
def addThinLines(proteinName, domainsFinal, startsToDomainList):
	domsStartsSorted = sorted(startsToDomainList.keys())
	# Used for taking care of situations when transmembrane domain is in the protein domain
	tempDom1End = None
	lastSmallDomainInBiggerDomain = False
	for ind in xrange(len(domsStartsSorted)-1):
		if len(startsToDomainList[domsStartsSorted[ind]]) == 1:
			# If not TM domain inside the protien domain was encountered
			if tempDom1End == None:
				dom1Start = domsStartsSorted[ind]
				dom1End = startsToDomainList[dom1Start][0]
				dom2Start = domsStartsSorted[ind+1]
				dom2End = startsToDomainList[dom2Start][0]
			# If previous TM domain was inside the protein domain
			else:
				dom1Start = domsStartsSorted[ind]
				dom1End = tempDom1End
				dom1EndTrue = startsToDomainList[dom1Start][0]
				dom2Start = domsStartsSorted[ind+1]
				dom2End = startsToDomainList[dom2Start][0]
				# Check if the second domain start is inside the big previouse domain
				# and make invisible line between dom1 and dom2
				if dom2Start < dom1End:
					thisThinLine = makeThinLine(dom1EndTrue+1, dom2Start-1, False)
					domainsFinal.append(thisThinLine)
			# If the second domain is TM/lowComplexity domain and it's inside the first protein domain
			if dom2End < dom1End:
				# this is the end of a larger domain inside which a second domain is placed
				tempDom1End = dom1End
				lastSmallDomainInBiggerDomain = True
					
			else:
				tempDom1End = None
				# we don't assign 'lastSmallDomainInBiggerDomain = True' because 
				# at next satge based on the dom2 and lastSmallDomainInBiggerDomain 
				# the thin line will be drown using the immdiate below clause
			if dom2Start > dom1End:
				if lastSmallDomainInBiggerDomain:
					lastSmallDomainInBiggerDomain = False
					thisThinLine = makeThinLine(dom1EndTrue+1, dom1End, False)
					domainsFinal.append(thisThinLine)
				thisThinLine = makeThinLine(dom1End+1, dom2Start-1)
				domainsFinal.append(thisThinLine)
			# case: dom1 is a large domain and dom2 is domain inside dom1 and 
			# dom2 is the last domain
			elif tempDom1End and ind == (len(domsStartsSorted)-2):
				thisThinLine = makeThinLine(dom2End+1, tempDom1End, False)
				domainsFinal.append(thisThinLine)
				
def makeThinLine(startPosition, endPosition, notInsideDomain=True):
	if notInsideDomain:
		thisThinLine = THIN_LINE[::]
		thisThinLine[0] = startPosition
		thisThinLine[1] = endPosition
	else:
		thisThinLine = INVISIBLE_LINE[::]
		thisThinLine[0] = startPosition
		thisThinLine[1] = endPosition
	return thisThinLine

def processFileWithDomains():
	with open(DOMAINS, "r") as domainsFile:
		global PROTEIN_DOMAINS
		PROTEIN_DOMAINS = json.load(domainsFile)
	
def processFileWithSeqs():
	with open(SEQS, "r") as seqs:
		for sequence in SeqIO.parse(seqs, "fasta"):
			PROTEIN_NAME_TO_SEQ[sequence.description.strip()] = str(sequence.seq)
	if SEQS_ALIGNED != None:		
		with open(SEQS_ALIGNED, "r") as alignedSeqs:
			for sequence in SeqIO.parse(alignedSeqs, "fasta"):
				ALIGNED_PROTEIN_NAME_TO_SEQ[sequence.description.strip()] = str(sequence.seq)                   #

def layout(node):
    if node.is_leaf():
		if node.name in PROTEIN_TO_DOMAINS:
			seq = PROTEIN_NAME_TO_SEQ[node.name]
			protDomains = PROTEIN_TO_DOMAINS[node.name]
			seqFace = SeqMotifFace(seq, protDomains, scale_factor=1.3)
			add_face_to_node(seqFace, node, 0, position="aligned")
       
def writeSeqsAndTree():
	prepareNameDict()
	tree = Tree(TREE_FILE)
	terminals = tree.get_leaves()
	# Change protein names in datas sctrucuters and write protein sequences with changed names to file 					
	if SEQS_ALIGNED != None:
		with open(OUTPUT_ALIGNED_FILENAME, "w") as outputFile:
			for i in xrange(len(terminals)):
				proteinName = terminals[i].name.strip("'")
				processedName = prepareName(proteinName)
				if processedName in PROCESSED_TO_ALIGNED_NAMES:
					terminals[i].name = " " + str(i+1) + "_" + proteinName
					if processedName in PROCESSED_TO_FEATURE_NAMES:
						featureName = PROCESSED_TO_FEATURE_NAMES[processedName]
						PROTEIN_DOMAINS[terminals[i].name] = PROTEIN_DOMAINS[featureName]
						del PROTEIN_DOMAINS[featureName]
					proteinSeqName = PROCESSED_TO_PROTEIN_NAMES[processedName] 
					PROTEIN_NAME_TO_SEQ[terminals[i].name] = PROTEIN_NAME_TO_SEQ[proteinSeqName]
					del PROTEIN_NAME_TO_SEQ[proteinSeqName]
					alignedName = PROCESSED_TO_ALIGNED_NAMES[processedName]					
					ALIGNED_PROTEIN_NAME_TO_SEQ[terminals[i].name] = ALIGNED_PROTEIN_NAME_TO_SEQ[alignedName]
					del ALIGNED_PROTEIN_NAME_TO_SEQ[alignedName]
					outputFile.write(">" + terminals[i].name + "\n")
					outputFile.write(str(ALIGNED_PROTEIN_NAME_TO_SEQ[terminals[i].name]) + "\n")		
	else:
		for i in xrange(len(terminals)):
			proteinName = terminals[i].name.strip("'")
			processedName = prepareName(proteinName)
			terminals[i].name = " " + str(i+1) + "_" + proteinName
			if processedName in PROCESSED_TO_FEATURE_NAMES:
				featureName = PROCESSED_TO_FEATURE_NAMES[processedName]
				PROTEIN_DOMAINS[terminals[i].name] = PROTEIN_DOMAINS[featureName]
				del PROTEIN_DOMAINS[featureName]
				proteinSeqName = PROCESSED_TO_PROTEIN_NAMES[processedName]				
				PROTEIN_NAME_TO_SEQ[terminals[i].name] = PROTEIN_NAME_TO_SEQ[proteinSeqName]
				del PROTEIN_NAME_TO_SEQ[proteinSeqName]											
						
	prepareProteinToDomainsDict()
	tree.write(outfile=OUTPUT_TREE_NEWICK_FILENAME)
	treeStyle = TreeStyle()
	treeStyle.layout_fn = layout
	tree.render(OUTPUT_TREE_SVG_FILENAME, w=800, dpi=400, tree_style=treeStyle)


def prepareNameDict():
	for protein in PROTEIN_DOMAINS:
		processdName = prepareName(protein)
		PROCESSED_TO_FEATURE_NAMES[processdName] = protein
	for protein in ALIGNED_PROTEIN_NAME_TO_SEQ:
		processdName = prepareName(protein)		
		PROCESSED_TO_ALIGNED_NAMES[processdName] = protein		
	for protein in PROTEIN_NAME_TO_SEQ:
		processdName = prepareName(protein)		
		PROCESSED_TO_PROTEIN_NAMES[processdName] = protein		

		
REGEX_UNDERSCORE = re.compile(r"(\W|_)")
REGEX_UNDERSCORE_SUBST = ""

def prepareName(line):
	return REGEX_UNDERSCORE.sub(REGEX_UNDERSCORE_SUBST, line)
	
def main(argv):
	initialyze(argv)
	processFileWithDomains()
	processFileWithSeqs()
	writeSeqsAndTree()

if __name__ == "__main__":
	main(sys.argv)
		
		
 


	
			
		




