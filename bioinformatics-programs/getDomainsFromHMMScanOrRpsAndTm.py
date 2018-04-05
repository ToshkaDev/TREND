#!/usr/bin/python

import sys, getopt, re
import collections
import traceback
import json
from subprocess import call

USAGE = "\n\nThe script extracts domain information from the results of hmmscan or rpsblast.\n\n" + \
	"python 	" + sys.argv[0] + '''
	-h                     - help
	-i || --ifile          - input protein fasta file
	-p || --iprocess       - 'hmmscan' or 'rpsblast'
	-r || --ofourth        - output file for hmmscan or rpsblast (and input for rpcbproc)
	-f || --ofifth         - output file for rpcbproc
	-x || --osixth         - output file for tmhmmscan
	-A || --hmmscanDbPath  - path to hmmscan database
	-B || --rpsblastDbPath - path to rpsblast database
	-C || --rpsprocDbPath  - path to database for rpcbproc
	-D || --rpsblastSpDb   - specific rpsblast database
	[-H || --hmmscanPath]  - hmmscan program full path
	[-R || --rpsblastPath] - rpsblast program full path
	[-P || --rpsbprocPath] - rpsbproc program full path
	[-T || --tmhmm2Path]   - tmhmm2 program full path
 	[-e || --evalue]       - e-value threshold of a recognized domain
	[-t || --tabformat]    - output file format as tab delimited file (yes||y or no||n, default no)
	[-j || --jsonformat]   - output file format as json (yes||y or no||n, default yes)
	[-u || --thread]       - number of threads for hmmscan or rpsblast; default=4
	[-m || --sites]        - shoud protein sites be included to the resulting file, default False
	[-o || --ofile]        - output file with domain information for each protein in tab delimited text format
	[-n || --osecond]      - output file with counts of each domain architecture variant in tab delimited text format
	[-d || --othird]       - output file  with domain information for each protein in json format
	'''
	
	
#Programs with full paths
HMMSCAN_PROGRAM = None
RPSBLAST_PROGRAM = None
RPSBPROC_PROGRAM = None
TMHMMSCAN_PROGRAM = None


# Global parameters specified via program arguments
PROTEIN_TO_DOMAININFO_FILE = "protein-to-domain-info.txt"
DOMAIN_ARCHITECT_TO_COUNT_FILE = "domainArchitec-to-count.txt"
PROTEIN_TO_DOMAININFO_JSONFILE = "proteinToDomainJson.json"
PROTEIN_TO_SITES_FILE = "proteinToSites.txt"
GET_JSON = True
GET_TAB = False
JOIN_STRING = True


INPUT_FILE_FASTA = None
PROCESS_TYPE = None
OUTPUT_RPSBLAST_OR_HMMSCAN = "HMMSCAN_RESULT"
OUTPUT_RPSBPROC = "RPSBPROC_RESULT"
OUTPUT_TMHMMSCAN = "TMHMMSCan_RESULT"
HMMSCAN_DB_PATH = "/home/Soft/hmmer/pfam31_0/Pfam-A.hmm"
RPSBLAST_DB_PATH = "/home/vadim/Softs/rpsblastdb/"
RPSBPROC_DB_PATH = "/home/vadim/Softs/rpsbproc/data/"
RPSBLAST_DB = "Cdd_NCBI"
CPU = 4


PROTEIN_SITES_INCLUDE = False
EVAL_THRESHOLD = 0.01

# Global parameters
BORDER_STYLES = ["jagged", "curved"]
HMMSCAN = "hmmscan"
RPSBLAST = "rpsblast"
DOMAIN = "DOMAIN"
PROTEIN_SITE = "PROTEIN_SITE"


# Global datastructures which are got initialized and manipulated by the program
PROTREF_TO_DOMAIN_INFO = collections.defaultdict(list)
PROTREF_TO_DOMAINS = dict()
PROT_NAME_TO_LENGTH = dict()
DOMAIN_ARCHITECT_TO_COUNT_DICT = collections.defaultdict(int)
PROTEIN_TO_SITES = collections.defaultdict(list)
    
def initialyze(argv):
	global HMMSCAN_PROGRAM, RPSBLAST_PROGRAM, RPSBPROC_PROGRAM, TMHMMSCAN_PROGRAM, INPUT_FILE_FASTA, PROCESS_TYPE, OUTPUT_RPSBLAST_OR_HMMSCAN, \
	OUTPUT_RPSBPROC, OUTPUT_TMHMMSCAN, HMMSCAN_DB_PATH, RPSBLAST_DB_PATH, RPSBPROC_DB_PATH, RPSBLAST_DB, CPU, EVAL_THRESHOLD, GET_TAB, GET_JSON, \
	PROTEIN_TO_DOMAININFO_FILE, DOMAIN_ARCHITECT_TO_COUNT_FILE, PROTEIN_TO_DOMAININFO_JSONFILE, PROTEIN_SITES_INCLUDE
	try:
		opts, args = getopt.getopt(argv[1:],"hi:p:r:f:x:A:B:C:D:H:R:P:T:e:t:j:u:m:o:n:d:",["ifile=", "iprocess=", "ofourth=", "ofifth=", "osixth=", "hmmscanDbPath=", \
		"rpsblastDbPath=", "rpsprocDbPath=", "rpsblastSpDb=", "hmmscanPath=", "rpsblastPath", "rpsbprocPath", "tmhmm2Path", "evalue=", "tabformat=", "jsonformat=", "thread=", "sites=", "ofile=", "osecond=", "othird="])
		if len(opts) == 0:
			raise getopt.GetoptError("Options are required\n")
	except getopt.GetoptError as e:
		print "===========ERROR==========\n " + str(e) + USAGE
		sys.exit(2)
	try:
		for opt, arg in opts:
			if opt == '-h':
				print USAGE
				sys.exit()
			elif opt in ("-i", "--ifile"):
				INPUT_FILE_FASTA = str(arg).strip()
			elif opt in ("-p", "--iprocess"):
				processType = str(arg).strip()
				if processType in ["hmmscan", "rpsblast"]:
					PROCESS_TYPE = processType
				else:
					raise Exception("-p || --iprocess argument should be 'hmmscan' or 'rpsblast'")
			elif opt in ("-u", "--thread"):
				CPU = str(arg).strip()
			elif opt in ("-t", "--ofourth"):
				OUTPUT_RPSBLAST_OR_HMMSCAN = str(arg).strip()
			elif opt in ("-f", "--ofifth"):
				OUTPUT_RPSBPROC = str(arg).strip()
			elif opt in ("-x", "--osixth"):
				OUTPUT_TMHMMSCAN = str(arg).strip()
			elif opt in ("-A", "--hmmscanDbPath"):
				HMMSCAN_DB_PATH = str(arg).strip()
			elif opt in ("-B", "--rpsblastDbPath"):
				RPSBLAST_DB_PATH = str(arg).strip()
			elif opt in ("-C", "--rpsprocDbPath"):
				RPSBPROC_DB_PATH = str(arg).strip()
			elif opt in ("-D", "--rpsblastSpDb"):
				RPSBLAST_DB = str(arg).strip()
			elif opt in ("-H", "--hmmscanPath"):
				HMMSCAN_PROGRAM = str(arg).strip()
			elif opt in ("-R", "--rpsblastPath"):
				RPSBLAST_PROGRAM = str(arg).strip()
			elif opt in ("-P", "--rpsbprocPath"):
				RPSBPROC_PROGRAM = str(arg).strip()
			elif opt in ("-T", "--tmhmm2Path"):
				TMHMMSCAN_PROGRAM = str(arg).strip()	
			elif opt in ("-e", "--evalue"):
				EVAL_THRESHOLD = float(arg)
			elif opt in ("-t", "--tabformat"):
				if str(arg) in ["no", "n"]:
					GET_TAB = False
				elif str(arg) in ["yes", "y"]:
					GET_TAB = True
				else:
					raise Exception("-t || --tabformat argument should be 'yes||y' or 'no||n'")
			elif opt in ("-j", "--jsonformat"):
				if str(arg) in ["no", "n"]:
					GET_JSON = False
				elif str(arg) in ["yes", "y"]:
					GET_JSON = True
				else:
					raise Exception("-j || --jsonformat argument should be 'yes||y' or 'no||n'")
			elif opt in ("-o", "--ofile"):
				PROTEIN_TO_DOMAININFO_FILE = str(arg).strip()
			elif opt in ("-n", "--osecond"):
				DOMAIN_ARCHITECT_TO_COUNT_FILE = str(arg).strip()
			elif opt in ("-d", "--othird"):
				PROTEIN_TO_DOMAININFO_JSONFILE = str(arg).strip()
			elif opt in ("-m", "--sites"):
				PROTEIN_SITES_INCLUDE = True
		if PROCESS_TYPE == None:
			raise Exception("-p || --iprocess is a mandatory parameter")
		if PROCESS_TYPE == RPSBLAST:
			if OUTPUT_RPSBLAST_OR_HMMSCAN == None or OUTPUT_RPSBPROC == None:
				raise Exception("-t and -f (||--ofourth and --ofifth) are both mandatory parameters if -p(||--iprocess) is 'rpsblast'")
	except Exception as e:
		print "===========ERROR==========\n " + str(e) + USAGE
		sys.exit(2)

class Protein(object):
	def __init__(self, length = None):
		self.domains = []
		self.tmInfo = dict()
		self.sites = []
		self.length = length
	def setRpsRegion(self, regionData, regionType):
		if regionType == DOMAIN:
			region = RpsDomainRegion()	
			region.name = regionData[9]
			region.start = regionData[4]
			region.end = regionData[5]
			region.source = regionData[8]
			region.eValue = regionData[6]
			region.bitscore = regionData[7]
			region.superfamily_pssmId = regionData[11]
			region.regionType = regionType
			domainEnds = regionData[10]
			if domainEnds == "-":
				region.startStyle = "complete"
				region.endStyle = "complete"
			elif domainEnds == "N":
				region.startStyle = "jagged"
				region.endStyle = "complete"
			elif domainEnds == "C":
				region.startStyle = "complete"
				region.endStyle = "jagged"
			elif domainEnds == "NC":
				region.startStyle = "jagged"
				region.endStyle = "jagged"
			self.domains.append(region)
		elif regionType == PROTEIN_SITE:
			region = RpsSiteRegion()
			region.name = regionData[3]
			region.sitePositions.append(regionData[4].split(","))
			region.complete_size = regionData[5]
			region.mapped_size = regionData[6]
			region.source_domain_pssmId = regionData[7]
			self.sites.append(region)
	def setHmmscanRegion(self, regionData, domain, description, regionType):
		if regionType == DOMAIN:
			region = HmmscanDomainRegion()	
			alignmentToModelType = regionData[8]
			modelStart = int(regionData[6])
			modelEnd = int(regionData[7])
			modelLen = modelEnd - modelStart + 1 
			region.modelStart = modelStart
			region.modelEnd = modelEnd
			region.aliStart = int(regionData[9])
			region.aliEnd = int(regionData[10])
			region.eValue = float(regionData[5])
			region.probability = float(regionData[2])
			region.modelLength = modelLen
			region.envStart = int(regionData[12])
			region.envEnd = int(regionData[13])
			region.source = "pfamA"
			region.description = description
			region.domainName = domain
			if alignmentToModelType == "[]":
				region.startStyle = "complete"
				region.endStyle = "complete"
			elif alignmentToModelType == ".]":
				region.startStyle = "jagged"
				region.endStyle = "complete"
			elif alignmentToModelType == "[.":
				region.startStyle = "complete"
				region.endStyle = "jagged"
			elif alignmentToModelType == "..":
				region.startStyle = "jagged"
				region.endStyle = "jagged"
		self.domains.append(region)
	def setTmInfo(self, first60=None, possibSigPep=None, tm=None, tmTopology=None):
		self.tmInfo["first60"] = first60
		self.tmInfo["possibSigPep"] = possibSigPep
		self.tmInfo["tmTopology"] = tmTopology
		self.tmInfo["tm"] = tm
		self.tmInfo["tmRegions"] = []
	def setTmRegions(self, tmSart, tmEnd):
		self.tmInfo["tmRegions"].append(TmRegion(tmSart, tmEnd))
		
class HmmscanDomainRegion(object):
	def __init__(self):
		self.domainName = None
		self.modelStart = None
		self.modelEnd = None
		self.aliStart = None
		self.aliEnd = None
		self.envStart = None
		self.envEnd = None
		self.colour = None
		self.startStyle = None
		self.endStyle = None
		self.display = None
		self.description = None
		self.modelLength = None
		self.source = None
		self.eValue = None
		self.probability = None
		self.regionType = None

class RpsSiteRegion(object):
	def __init__(self):
		self.name = None
		self.complete_size = None
		self.mapped_size = None
		self.source_domain_pssmId = None
		self.regionType = None
		self.sitePositions = []
		
class RpsDomainRegion(object):
	def __init__(self):
		self.regionType = None
		self.name = None
		self.start = None
		self.end = None
		self.colour = None
		self.startStyle = None
		self.endStyle = None
		self.display = None
		self.description = None
		self.source = None
		self.eValue = None
		self.bitscore = None
		self.superfamily_pssmId = None

class TmRegion(object):
	def __init__(self, tmSart, tmEnd):
		self.tmSart = tmSart
		self.tmEnd = tmEnd
				
def processHmmscan():
	domainListBegan = False
	hitFound = False
	currentQuery = None
	proteinObject = None
	proteinName = None
	regex = re.compile(r"\s")
	try:	
		with open(OUTPUT_RPSBLAST_OR_HMMSCAN, "r") as hmmscanFile:
			for record in hmmscanFile:
				record = record.strip()
				recSplitted = record.split(":")
				if recSplitted[0] == "Query":
					proteinAndLengthList = recSplitted[1].split("[")
					#If record is UniprotKb; need to investigate this
					if "|" in recSplitted[1]:
						if recSplitted[1][:2] == 'tr':
							currentQuery = proteinAndLengthList[0].split("|")[2].strip()
						else:
							currentQuery = proteinAndLengthList[0].split("|")[3].strip()
					else:
						currentQuery = proteinAndLengthList[0].strip()
					proteinlen = proteinAndLengthList[1].strip().rstrip("]")
					PROT_NAME_TO_LENGTH[currentQuery] = proteinlen
					if GET_JSON:
						proteinObject = Protein(proteinlen.split("=")[1])
						PROTREF_TO_DOMAINS[currentQuery] = proteinObject
				elif recSplitted[0] == "Description":
					record = record.replace("Description: ", "")
					# If query should be joined in one string
					if JOIN_STRING:
						currentQueryOld = currentQuery
						currentQuery = currentQuery + "_" + record.replace(" ", "_").strip()
					else:
						currentQueryOld = currentQuery
						currentQuery = currentQuery + " " + record.strip()
					PROT_NAME_TO_LENGTH[currentQuery] = PROT_NAME_TO_LENGTH[currentQueryOld]
					del PROT_NAME_TO_LENGTH[currentQueryOld]	
					if GET_JSON:
						PROTREF_TO_DOMAINS[currentQuery] = proteinObject
						del PROTREF_TO_DOMAINS[currentQueryOld]
				elif record[:2] == ">>":
					hitFound = True
					domain = record.split(" ")[1] #identifier and text
					domainDescription = " ".join(record.split(" ")[2:]).strip() #description
				elif hitFound and record[:3] == "---":
					domainListBegan = True
				elif len(record) > 0 and "#" not in record:
					if hitFound and domainListBegan and record != 'Alignments for each domain:' and record != "Internal pipeline statistics summary:":
						recordListWithoutSpaces = [elem for elem in record.split(" ") if len(elem) > 0]
						if float(recordListWithoutSpaces[4]) < EVAL_THRESHOLD:
							envStart = int(recordListWithoutSpaces[12])
							envEnd = int(recordListWithoutSpaces[13])
							if GET_JSON:
								proteinObject.setHmmscanRegion(recordListWithoutSpaces, domain, domainDescription, DOMAIN)
							PROTREF_TO_DOMAIN_INFO[currentQuery].append([domain, envStart, envEnd])
					if record == "Alignments for each domain:" or record == "Internal pipeline statistics summary:":
						hitFound = False
						domainListBegan = False		
	except Exception, e:
		print "Problem happened: ", traceback.print_exc()
		print "record", record
	finally:
		hmmscanFile.close()

def processRpsbproc():
	domainListBegan = False
	siteListBegan = False
	with open(OUTPUT_RPSBPROC, "r") as rpsbprocFile:
		for record in rpsbprocFile:
			recordList = record.strip().split("\t")
			if recordList[0] == "QUERY":
				proteinLength = recordList[3]
				query = recordList[4]
				if JOIN_STRING:
					query = recordList[4].replace(" ", "_")
				if GET_TAB:
					PROT_NAME_TO_LENGTH[query] = proteinLength
				if GET_JSON:
					proteinObject = Protein(proteinLength)
					PROTREF_TO_DOMAINS[query] = proteinObject
			elif recordList[0] == "DOMAINS":
				domainListBegan = True
			elif recordList[0] != "ENDDOMAINS" and domainListBegan:
				if GET_TAB:
					domainName = recordList[9]
					start = recordList[4]
					end = recordList[5]
					PROTREF_TO_DOMAIN_INFO[query].append([domainName, start, end])
				if GET_JSON:
					proteinObject.setRpsRegion(recordList, DOMAIN)
			elif recordList[0] == "ENDDOMAINS":
				domainListBegan = False
			elif PROTEIN_SITES_INCLUDE and recordList[0] == "SITES":
				siteListBegan = True
			elif PROTEIN_SITES_INCLUDE and recordList[0] != "ENDSITES" and siteListBegan:
				if GET_TAB:
					siteName = recordList[3]
					sites = recordList[4]
					completeSize = recordList[5]
					mappedSize = recordList[6]
					sorceDomainPssmId = recordList[7]
					PROTEIN_TO_SITES[query].append([siteName, sites, completeSize, mappedSize, sorceDomainPssmId])
				if GET_JSON:
					proteinObject.setRpsRegion(recordList, PROTEIN_SITE)
			elif PROTEIN_SITES_INCLUDE and recordList[0] == "ENDSITES":
				siteListBegan = False
				
def processTmscan():
	with open(OUTPUT_TMHMMSCAN, "r") as tmmscanFile:
		for line in tmmscanFile:
			possibSigPep = "no"
			tm = "no"
			tmTopology = None
			topologyCoords = None
			line = line.strip().split("\t")
			if JOIN_STRING:
				query = line[0].replace(" ", "_")
			proteinLen = line[1].split("=")[1]
			numberOfHelices = float(line[4].split("=")[1])
			first60AA = line[3].split("=")
			first60 = first60AA[1]
			if float(first60AA[1]) > 10:
				possibSigPep = "yes"
			if numberOfHelices > 0:
				tm = "yes"
				tmTopology = line[5].split("=")[1].replace("o", "i").strip("i")
				topologyCoords = tmTopology.split("i")
				#set Tm info on the existing protein object			
				PROTREF_TO_DOMAINS[query].setTmInfo(first60, possibSigPep, tm, tmTopology)
				for coordPair in topologyCoords:
					PROTREF_TO_DOMAINS[query].setTmRegions(coordPair.split("-")[0], coordPair.split("-")[1])

def main(argv):
	initialyze(argv)
	if PROCESS_TYPE == HMMSCAN:
		print "Here1"
		hmmscan()
		processHmmscan()
	elif PROCESS_TYPE == RPSBLAST:
		print "Here2"
		rpsBlast()
		processRpsbproc()
	tmhmm2scan()
	processTmscan()
	if GET_TAB:
		with open(PROTEIN_TO_DOMAININFO_FILE, "w") as proteinToDomainFile:
			for proteint in PROT_NAME_TO_LENGTH:
				domainArchitecture = getDomainArchitecture(proteint, PROTREF_TO_DOMAIN_INFO)
				readyList = getListOfDomains(proteint, PROTREF_TO_DOMAIN_INFO)
				protLen = PROT_NAME_TO_LENGTH[proteint]
				proteinToDomainFile.write("\t".join([proteint, domainArchitecture, protLen, ";".join(readyList)]) + "\n")
		with open(DOMAIN_ARCHITECT_TO_COUNT_FILE, "w") as domainArchToCountFile:
			domainToCountList = sorted(DOMAIN_ARCHITECT_TO_COUNT_DICT.items(), key=lambda a: a[1], reverse=True)
			for domainArchitect in domainToCountList:
				domainArchToCountFile.write(domainArchitect[0] + "\t" + str(domainArchitect[1]) + "\n")
		with open(PROTEIN_TO_SITES_FILE, "w") as proteinToSitesFile:
			for protein in PROTEIN_TO_SITES:
				for site in PROTEIN_TO_SITES[protein]:
					proteinToSitesFile.write(protein + "\t" +  "\t".join(site) + "\n")
			
	if GET_JSON:
		with open(PROTEIN_TO_DOMAININFO_JSONFILE, "w") as proteinToDomainJson:
				json.dump(PROTREF_TO_DOMAINS, proteinToDomainJson, 
				default=lambda o: o.__dict__, 
				sort_keys=True, indent=4)

def getSortedListOfDomains(proteint, protRefToDomainInfo):
	return sorted(protRefToDomainInfo[proteint], key=lambda a: a[1])

def getListOfDomains(proteint, protRefToDomainInfo):
	sortedListOfDomains = getSortedListOfDomains(proteint, protRefToDomainInfo)
	readyList = [sublst[0] + ':' + str(sublst[1]) + '-' + str(sublst[2]) for sublst in sortedListOfDomains]
	return readyList

#prepare proteins and their domains information for writing to the file
def getDomainArchitecture(proteint, protRefToDomainInfo):
	sortedListOfDomains = getSortedListOfDomains(proteint, protRefToDomainInfo)
	domainArchitecture = "-".join([sublst[0] for sublst in sortedListOfDomains])
	DOMAIN_ARCHITECT_TO_COUNT_DICT[domainArchitecture]+=1
	return domainArchitecture

def hmmscan():
	#if hmmscan is in the linux path, this will be resloved
	hmmscan = "hmmscan"
	#If hmmscan is not in the linux path then give it the program full path as a parameter. The same for 'rpsblast', 'rpsbproc' and 'tmhmm' programs
	if HMMSCAN_PROGRAM != None:
		hmmscan = HMMSCAN_PROGRAM
	runSubProcess(" ".join([hmmscan, "--noali", "--cpu", str(CPU), HMMSCAN_DB_PATH, INPUT_FILE_FASTA, ">", OUTPUT_RPSBLAST_OR_HMMSCAN]))
	
def rpsBlast():
	rpsblast = "rpsblast"
	rpsbproc = "rpsbproc"
	if RPSBLAST_PROGRAM != None:
		rpsblast = RPSBLAST_PROGRAM
	if RPSBPROC_PROGRAM != None:
		rpsbproc = RPSBPROC_PROGRAM
		
	runSubProcess(" ".join([rpsblast, "--thread", CPU, "-evalue", "0.01", "-seg", "no", "-outfmt", "5", "-db", RPSBLAST_DB_PATH+RPSBLAST_DB, "-query", INPUT_FILE_FASTA, "-out", OUTPUT_RPSBLAST_OR_HMMSCAN]))
	runSubProcess(" ".join([rpsbproc, "-d", RPSBPROC_DB_PATH, "-i", OUTPUT_RPSBLAST_OR_HMMSCAN, "-o", OUTPUT_RPSBPROC]))
	
def tmhmm2scan():
	tmhmm = "tmhmm"
	if TMHMMSCAN_PROGRAM != None:
		tmhmm = TMHMMSCAN_PROGRAM
	runSubProcess(" ".join([tmhmm, "--short", INPUT_FILE_FASTA, ">", OUTPUT_TMHMMSCAN]))

def runSubProcess(command):
	try:
		call(command, shell=True)
	except OSError, osError:
		print "osError " + osError
		print traceback.print_exc()
		
		
if __name__ == "__main__":
	main(sys.argv)

























