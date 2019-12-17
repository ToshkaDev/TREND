#!/usr/bin/python

import sys, getopt, os, time
import collections
import traceback
from subprocess import Popen


USAGE = "\nThis script align proteins using mafft in subporcess and build a phylogenetic tree \n\n" + "python" + sys.argv[0] + '''
-i || --isequence         -input filew with sequences
-d || --do_align          -should the input be aligned ("yes", or "no")
-a || --al_algorithm      -algorithm to align equences; options: --localpair | --genafpair | --globalpair | --retree 2 | --retree 2 --maxiterate 0
-r || --reorder           -reorder or not (type --reorder if reorder)
[-k || --megacc]          -full path to megacc
[-f || --mafft]           -full path to mafft
[-t || --thread]          -number of threads to use to align equences (default is 4)
[-o || --oaligned]        -output file with aligned sequences
[-m || --tree_method]     -method to build a tree; options:  NJ, ML, ME (default is NJ)
[-l || --subst_model]     -amino acids substitution model (default is Jones-Taylor-Thornton (JTT) model)
			For NJ and ME trees  -l is one of the following: {"jtt": "Jones-Taylor-Thornton (JTT) model", "nd": "No. of differences", "eim": "Equal input model",
		"pd": "p-distance", "pm": "Poisson model", "dm": "Dayhoff model"}
			For ML tree: {"jtt": "Jones-Taylor-Thornton (JTT) model", "pm": "Poisson model", "eim": "Equal input model",
		"dm": "Dayhoff model", "dmf": "Dayhoff model with Freqs. (F+)", "jttf": "JTT with Freqs. (F+) model", "wm": "WAG model",
		"wmf": "WAG with Freqs. (F+) model", "lg": "LG model", "lgf": "LG with Freqs. (F+) model", "grm": "General Reversible Mitochondrial (mtREV)",
		"grmf": "mtREV with Freqs. (F+) model", "grc": "General Reversible Chloroplast (cpREV)", "grcf": "cpREV with Freqs. (F+) model",
		"grt": "General Reversible Transcriptase model (rtREV)", "grtf": "rtREV with Freqs. (F+) model"}
[-g || --gaps_missing]    -how to treat gaps and missing data; one of the following: {"compDel": "Complete deletion", "partDel": "Partial deletion", "pairDel": "Pairwise deletion"}; defaultis "Complete deletion"
[-c || --coverage_cutoff] -Site Coverage Cutoff (%) if -g (||--gaps_missing) is set to "Partial deletion"
[-u || --cpu]             -number of threads to use for a tree building (default is 4)
[-p || --phylo]           -phylogeny test; for NJ and ME one of the following: {"none": "None", "bm": "Bootstrap method", "ib": "Interior-branch test"};
		for ML: {"none": "None", "bm": "Bootstrap method"}; default is "None"
[-b || --bootstrap]       -number of replicates for the bootstrap testing; if not provided the bootstrap test will not run
[-e || --initial_tree]    -initial tree for ML; on of the following: {"njBio": "Make initial tree automatically (Default - NJ/BioNJ)", "mp": "Make initial tree automatically (Maximum parsimony)", "nj": "Make initial tree automatically (Neighbor joining)",
			"Make initial tree automatically (BioNJ)"}; default is "Make initial tree automatically (Default - NJ/BioNJ)"
[-n || --subst_rate]      -amino acids substitution rate; on of the following: {"ur": "Uniform Rates", "gd": "Gamma Distributed (G)", "ir": "Has Invariant Sites (I)", "gir":"Gamma Distributed With Invariant Sites (G+I)"};
		default is "Uniform Rates"
[-x || --otree_params]    -output file with parameters for megacc tree building (extension is '.mao')
[-z || --otree]           -output file with the constuctred tree
'''



#Inputs
INPUT_FILE = "input.fa"
DO_ALIGN = True
ALGORITHM = "--localpair"
REORDER_OR_NOT = ""
ALIGN_THREADS = "4"

#Outputs
OUTPUT_FILE_FIRST = "aligned_proteins.fa"
OUTPUT_FILE_SECOND = "paramsForTree.mao"
OUTPUT_FILE_THIRD = "newTree"


TREE_BUILDING_PROGRAM = "fast-tree"
FAST_TREE_PROGRAM = "FstTreeMP"

#Options
ML_PHYLOGENY_TESTS = {"none": "None", "bm": "Bootstrap method"}
NJ_ME_PHYLOGENY_TESTS = {"none": "None", "bm": "Bootstrap method", "ib": "Interior-branch test"}
RATES = {"ur": "Uniform Rates", "gd": "Gamma Distributed (G)", "ir": "Has Invariant Sites (I)", "gir":"Gamma Distributed With Invariant Sites (G+I)"}
NJ_ME_SUBSTITUTIAN_MODELS = {"jtt": "Jones-Taylor-Thornton (JTT) model", "nd": "No. of differences", "eim": "Equal input model",
"pd": "p-distance", "pm": "Poisson model", "dm": "Dayhoff model"}
ML_SUBSTITUTIAN_MODELS = {"jtt": "Jones-Taylor-Thornton (JTT) model", "pm": "Poisson model", "eim": "Equal input model",
"dm": "Dayhoff model", "dmf": "Dayhoff model with Freqs. (F+)", "jttf": "JTT with Freqs. (F+) model", "wm": "WAG model",
"wmf": "WAG with Freqs. (F+) model", "lg": "LG model", "lgf": "LG with Freqs. (F+) model", "grm": "General Reversible Mitochondrial (mtREV)",
"grmf": "mtREV with Freqs. (F+) model", "grc": "General Reversible Chloroplast (cpREV)", "grcf": "cpREV with Freqs. (F+) model",
"grt": "General Reversible Transcriptase model (rtREV)", "grtf": "rtREV with Freqs. (F+) model"}
GAPS_AND_MISSING_DATA_BEHAVIOUR = {"compDel": "Complete deletion", "partDel": "Partial deletion", "pairDel": "Pairwise deletion"}
ML_INITIAL_TREE_OPTIONS = {"njBio": "Make initial tree automatically (Default - NJ/BioNJ)", "mp": "Make initial tree automatically (Maximum parsimony)", "nj": "Make initial tree automatically (Neighbor joining)",
"bioNj": "Make initial tree automatically (BioNJ)"}

#Parameters
#LINUX_VERSION = "7170509-x86_64 Linux"
LINUX_VERSION = "7160929-x86_64 Linux"
MISSING_SYMBOL = "?"
IDENTICAL_SYMBOL = "."
GAP_SYMBOL = "-"
NEW_SUBSECTION ="===================="
NOT_APPLICABLE = "Not Applicable"
DEFAULT_PATTERN_AMONG_LINEAGES = "Same (Homogeneous)"
TEST_OF_PHYLOGENY = "None"
BOOTSTRAPS = NOT_APPLICABLE
DEFAULT_ML_HEURISTIC_METHOD = "Nearest-Neighbor-Interchange (NNI)"
BOOTSTRAP_CPU_NUMBER = "1"
SUBST_MODEL = NJ_ME_SUBSTITUTIAN_MODELS["jtt"]
GAPS_MISSING = GAPS_AND_MISSING_DATA_BEHAVIOUR["compDel"]
COVERAGE_CUTOFF = NOT_APPLICABLE
TREE_THREADS = "4"
ML_INITIAL_TREE = ML_INITIAL_TREE_OPTIONS["njBio"]
SUBST_RATE = RATES["ur"]
PHYLOGENY_TEST_HEADER = {"NJ": NOT_APPLICABLE, "ML_ME": NEW_SUBSECTION}
PROCESS_TYPES = collections.OrderedDict()
PROCESS_TYPES_DICT = {"ML": "ppML", "ME": "ppME", "NJ": "ppNJ"}
TREE_METHOD = "NJ"
TREE_METHODS = {"NJ": None, "ML": None, "ME": None}
MAFFT_PROGRAM = None
MEGACC_PROGRAM = None


#FastTree options:
#default test is Shimodaira-Hasegawa test
PHYLOGENY_TEST_FT = ""
SUBST_MODEL_FT = ""
#deafult is not to use
GAMMA_TWENTY_FT = ""
PSEUDOCOUNTS_FT = ""
NUMBER_OF_REPLICATES_FT = 1000



def initialyze(argv):
	global INPUT_FILE, DO_ALIGN, ALGORITHM, REORDER_OR_NOT, MAFFT_PROGRAM, MEGACC_PROGRAM, ALIGN_THREADS, OUTPUT_FILE_FIRST, TREE_METHOD, SUBST_MODEL, GAPS_MISSING, \
	COVERAGE_CUTOFF, TREE_THREADS, TEST_OF_PHYLOGENY, BOOTSTRAPS, ML_INITIAL_TREE, SUBST_RATE, OUTPUT_FILE_SECOND, OUTPUT_FILE_THIRD, TREE_METHODS, BOOTSTRAP_CPU_NUMBER
	global PHYLOGENY_TEST_FT, SUBST_MODEL_FT, GAMMA_TWENTY_FT, PSEUDOCOUNTS_FT, NUMBER_OF_REPLICATES_FT, TREE_BUILDING_PROGRAM
	try:
		opts, args = getopt.getopt(argv[1:],"hi:d:a:r:k:f:t:o:m:l:g:c:u:p:b:e:n:x:z:P:M:G:S:R:B:",
		["isequence=", "do_align=", "al_algorithm=", "reorder=", "megacc", "mafft=", "thread=", "oaligned=",
		"tree_method=", "subst_model=", "gaps_missing=", "coverage_cutoff=",
		"cpu=", "phylotest=" "bootstrap=", "initial_tree=", "subst_rate=", "otree_params", "otree=", "phylotest_ft=",
		"subst_model_ft=", "gamm20_ft=", "pseudocounts_ft=", "bootstrap_ft=", "tree_program="])
		if len(opts) == 0:
			raise getopt.GetoptError("Parameters are required\n")
	except getopt.GetoptError as e:
		print "===========ERROR==========\n " + str(e) + USAGE
		sys.exit(2)
	for opt, arg in opts:
		if opt == '-h':
			print USAGE
			sys.exit()
		elif opt in ("-i", "--isequence"):
			INPUT_FILE = str(arg).strip()
		elif opt in ("-a", "--al_algorithm"):
			param = str(arg).strip()
			if param in ["--maxiterate 1000 --localpair", "--maxiterate 1000 --genafpair", "--maxiterate 1000 --globalpair", "--retree 2 --maxiterate 1000", "--retree 2 --maxiterate 0"]:
				ALGORITHM = param
		elif opt in ("-d", "--do_align"):
			param = str(arg).strip()
			if param == "no":
				DO_ALIGN = False
		elif opt in ("-r", "--reorder"):
			param = str(arg).strip()
			REORDER_OR_NOT = param
		elif opt in ("-k", "--megacc"):
			MEGACC_PROGRAM = str(arg).strip()
		elif opt in ("-f", "--mafft"):
			MAFFT_PROGRAM = str(arg).strip()
		elif opt in ("-t", "--thread"):
			ALIGN_THREADS = str(arg).strip()
		elif opt in ("-o", "--oaligned"):
			OUTPUT_FILE_FIRST = str(arg).strip()
		elif opt in ("-m", "--tree_method"):
			param = str(arg).strip()
			if param in TREE_METHODS:
				TREE_METHOD = param
		elif opt in ("-l", "--subst_model"):
			param = str(arg).strip()
			if param in NJ_ME_SUBSTITUTIAN_MODELS:
				SUBST_MODEL = NJ_ME_SUBSTITUTIAN_MODELS[param]
			elif param in ML_SUBSTITUTIAN_MODELS:
				SUBST_MODEL = ML_SUBSTITUTIAN_MODELS[param]
		elif opt in ("-g", "--gaps_missing"):
			param = str(arg).strip()
			if param in GAPS_AND_MISSING_DATA_BEHAVIOUR:
				GAPS_MISSING = GAPS_AND_MISSING_DATA_BEHAVIOUR[param]
		elif opt in ("-c", "--coverage_cutoff"):
			COVERAGE_CUTOFF = str(arg).strip()
		elif opt in ("-u", "--cpu"):
			TREE_THREADS = str(arg).strip()
		elif opt in ("-p", "--phylo"):
			param = str(arg).strip()
			if param in NJ_ME_PHYLOGENY_TESTS:
				TEST_OF_PHYLOGENY = NJ_ME_PHYLOGENY_TESTS[param]
			elif param in ML_PHYLOGENY_TESTS:
				TEST_OF_PHYLOGENY = ML_PHYLOGENY_TESTS[param]
		elif opt in ("-b", "--bootstrap"):
			param = str(arg).strip()
			if len(param) > 0:
				BOOTSTRAPS = param
		elif opt in ("-e", "--initial_tree"):
			param = str(arg).strip()
			if param in ML_INITIAL_TREE_OPTIONS:
				ML_INITIAL_TREE = ML_INITIAL_TREE_OPTIONS[param]
		elif opt in ("-n", "--subst_rate"):
			param = str(arg).strip()
			if param in RATES:
				SUBST_RATE = RATES[param]
		elif opt in ("-x", "--otree_params"):
			OUTPUT_FILE_SECOND = str(arg).strip()
		elif opt in ("-z", "--otree"):
			OUTPUT_FILE_THIRD = str(arg).strip()
		elif opt in ("-P", "--phylotest_ft"):
			PHYLOGENY_TEST_FT = str(arg).strip()
		elif opt in ("-M", "--subst_model_ft"):
			SUBST_MODEL_FT = str(arg).strip()
		elif opt in ("-G", "--gamm20_ft"):
			GAMMA_TWENTY_FT = str(arg).strip()
		elif opt in ("-S", "--pseudocounts_ft"):
			PSEUDOCOUNTS_FT = str(arg).strip()
		elif opt in ("-R", "--bootstrap_ft"):
			NUMBER_OF_REPLICATES_FT = str(arg).strip()
		elif opt in ("-B", "--tree_program"):
			TREE_BUILDING_PROGRAM = str(arg).strip()
	if TEST_OF_PHYLOGENY == "None":
		BOOTSTRAPS = NOT_APPLICABLE
	# if bootsrap test was set up use TREE_THREADS number of threads for bootstrapping
	else:
		BOOTSTRAP_CPU_NUMBER = TREE_THREADS
	if GAPS_MISSING != GAPS_AND_MISSING_DATA_BEHAVIOUR["partDel"]:
		COVERAGE_CUTOFF = NOT_APPLICABLE

	print "TEST_OF_PHYLOGENY " + TEST_OF_PHYLOGENY
	print "BOOTSTRAPS " + BOOTSTRAPS
	print "GAPS_MISSING " + GAPS_MISSING
	print "COVERAGE_CUTOFF " + COVERAGE_CUTOFF
	print "TREE_THREADS " + TREE_THREADS

	PROCESS_TYPES["ppInfer"] = "true"
	specificPocessTypes = PROCESS_TYPES_DICT[TREE_METHOD]
	PROCESS_TYPES[specificPocessTypes] = "true"
	initializeTreeParams()


def initializeTreeParams():
	NJ_PARAMETERS = {
		"[ MEGAinfo ]":{
			"ver": LINUX_VERSION
		},
		"[ DataSettings ]":{
			"datatype": "snProtein",
			"MissingBaseSymbol": MISSING_SYMBOL,
			"IdenticalBaseSymbol": IDENTICAL_SYMBOL,
			"GapSymbol": GAP_SYMBOL
		},
		"[ ProcessTypes ]":PROCESS_TYPES,
		"[ AnalysisSettings ]":{
			"Analysis": "Phylogeny Reconstruction",
			"Scope": "All Selected Taxa",
			"Statistical Method": "Neighbor-joining",
			"Phylogeny Test": PHYLOGENY_TEST_HEADER["NJ"],
			"Test of Phylogeny": TEST_OF_PHYLOGENY,
			"No. of Bootstrap Replications": BOOTSTRAPS,
			"Substitution Model": NEW_SUBSECTION,
			"Substitutions Type": "Amino acid",
			"Model/Method": SUBST_MODEL,
			"Rates and Patterns": NEW_SUBSECTION,
			"Rates among Sites": SUBST_RATE,
			"Gamma Parameter": NOT_APPLICABLE,
			"Pattern among Lineages": DEFAULT_PATTERN_AMONG_LINEAGES,
			"Data Subset to Use": NEW_SUBSECTION,
			"Gaps/Missing Data Treatment": GAPS_MISSING,
			"Site Coverage Cutoff (%)": COVERAGE_CUTOFF,
			"Has Time Limit": "False",
			"Number of Threads": BOOTSTRAP_CPU_NUMBER,
			"Maximum Execution Time": "-1"
		}
	}

	ML_PARAMETERS = {
		"[ MEGAinfo ]":{
			"ver": LINUX_VERSION
		},
		"[ DataSettings ]":{
			"datatype": "snProtein",
			"MissingBaseSymbol": MISSING_SYMBOL,
			"IdenticalBaseSymbol": IDENTICAL_SYMBOL,
			"GapSymbol": GAP_SYMBOL
		},
		"[ ProcessTypes ]":PROCESS_TYPES,
		"[ AnalysisSettings ]":{
			"Analysis": "Phylogeny Reconstruction",
			"Scope": "All Selected Taxa",
			"Statistical Method": "Maximum Likelihood",
			"Phylogeny Test": PHYLOGENY_TEST_HEADER["ML_ME"],
			"Test of Phylogeny": TEST_OF_PHYLOGENY,
			"No. of Bootstrap Replications": BOOTSTRAPS,
			"Substitution Model": NEW_SUBSECTION,
			"Substitutions Type": "Amino acid",
			"Model/Method": SUBST_MODEL,
			"Rates and Patterns": NEW_SUBSECTION,
			"Rates among Sites": SUBST_RATE,
			"No of Discrete Gamma Categories": NOT_APPLICABLE,
			"Data Subset to Use": NEW_SUBSECTION,
			"Gaps/Missing Data Treatment": GAPS_MISSING,
			"Site Coverage Cutoff (%)": COVERAGE_CUTOFF,
			"Tree Inference Options": NEW_SUBSECTION,
			"ML Heuristic Method": DEFAULT_ML_HEURISTIC_METHOD,
			"Initial Tree for ML": ML_INITIAL_TREE,
			"Branch Swap Filter": "None",
			"System Resource Usage": NEW_SUBSECTION,
			"Number of Threads": TREE_THREADS,
			"Has Time Limit": "False",
			"Maximum Execution Time": "-1"
		}
	}

	ME_PARAMETERS = {
		"[ MEGAinfo ]":{
			"ver": LINUX_VERSION
		},
		"[ DataSettings ]":{
			"datatype=": "snProtein",
			"MissingBaseSymbol": MISSING_SYMBOL,
			"IdenticalBaseSymbol": IDENTICAL_SYMBOL,
			"GapSymbol": GAP_SYMBOL
		},
		"[ ProcessTypes ]":PROCESS_TYPES,
		"[ AnalysisSettings ]":{
			"Analysis": "Phylogeny Reconstruction",
			"Scope": "All Selected Taxa",
			"Statistical Method": "Minimum Evolution method",
			"Phylogeny Test": PHYLOGENY_TEST_HEADER["ML_ME"],
			"Test of Phylogeny": TEST_OF_PHYLOGENY,
			"No. of Bootstrap Replications": BOOTSTRAPS,
			"Substitution Model": NEW_SUBSECTION,
			"Substitutions Type": "Amino acid",
			"Model/Method": SUBST_MODEL,
			"Rates and Patterns": NEW_SUBSECTION,
			"Rates among Sites": SUBST_RATE,
			"Gamma Parameter": NOT_APPLICABLE,
			"Pattern among Lineages": DEFAULT_PATTERN_AMONG_LINEAGES,
			"Data Subset to Use": NEW_SUBSECTION,
			"Gaps/Missing Data Treatment": GAPS_MISSING,
			"Site Coverage Cutoff (%)": COVERAGE_CUTOFF,
			"Tree Inference Options": NEW_SUBSECTION,
			"ME Heuristic Method": "Close-Neighbor-Interchange (CNI)",
			"Initial Tree for ME": "Obtain initial tree by Neighbor-Joining",
			"ME Search Level": "1",
			"Has Time Limit": "False",
			"Number of Threads": BOOTSTRAP_CPU_NUMBER,
			"Maximum Execution Time": "-1"
		}
	}
	global TREE_METHODS
	TREE_METHODS = {"NJ": NJ_PARAMETERS, "ML": ML_PARAMETERS, "ME": ME_PARAMETERS}


def align_sequences():
	mafft = "mafft"
	if DO_ALIGN:
		if MAFFT_PROGRAM != None:
			mafft = MAFFT_PROGRAM
		runSubProcess(" ".join([mafft, ALGORITHM, REORDER_OR_NOT, "--thread", ALIGN_THREADS, INPUT_FILE, ">", OUTPUT_FILE_FIRST]), "align_sequences()")
	else:
		with open(INPUT_FILE, "r") as inputFile, open(OUTPUT_FILE_FIRST, "w") as outputFile:
			outputFile.write(inputFile.read())

def buildMegaTree():
	megacc = "megacc"
	if MEGACC_PROGRAM != None:
		megacc = MEGACC_PROGRAM
	treeMethod = TREE_METHODS[TREE_METHOD].copy()
	with open(OUTPUT_FILE_SECOND, "w") as paramOutputFile:
		currentSection = ""
		for element, subelement in treeMethod.items():
			currentSection = element
			paramOutputFile.write(element + "\n")
			for param, val in subelement.items():
				paramOutputFile.write(" = ".join([param, val]) + "\n")
	runSubProcess(" ".join([megacc, "-a", OUTPUT_FILE_SECOND, "-d", OUTPUT_FILE_FIRST, "-n", "-o", OUTPUT_FILE_THIRD]), "buildTree()")

def buildFastTree():
	fastTree = "FastTreeMP"
	if FAST_TREE_PROGRAM != None:
		fastTree = FAST_TREE_PROGRAM
	runSubProcess(" ".join([fastTree, PHYLOGENY_TEST_FT, SUBST_MODEL_FT, GAMMA_TWENTY_FT, PSEUDOCOUNTS_FT, "-boot ", NUMBER_OF_REPLICATES_FT, OUTPUT_FILE_FIRST, ">", OUTPUT_FILE_THIRD]), "buildTree()")

def runSubProcess(command, processName):
	try:
		proc = Popen(command, shell=True)
		status = proc.poll()
		while status == None:
			#print "Still runnig " + processName
			time.sleep(0.05)
			status = proc.poll()
		#print "Seems like finished " + processName + " " + str(status)
	except OSError, osError:
		print "osError " + osError
		print traceback.print_exc()

def main(argv):
	initialyze(argv)
	align_sequences()
	if TREE_BUILDING_PROGRAM == "fast-tree":
		buildFastTree()
	else:
		buildMegaTree()

if __name__ == "__main__":
	main(sys.argv)