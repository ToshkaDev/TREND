#!/usr/bin/python

import sys, getopt
import collections
import traceback
from subprocess import call


USAGE = "\nThis script align proteins using mafft in subporcess and build a phylogenetic tree \n\n" + "python" + sys.argv[0] + '''
-i || --isequence         -input filew with sequences
-a || --al_algorithm      -algorithm to align equences; options: --localpair | --genafpair | --globalpair | --retree 2 | --retree 2 --maxiterate 0
-r || --reorder           -reorder or not (type --reorder if reorder)
[-k || --megacc]          -full path to megacc
[-f || --mafft]           -full path to mafft
[-t || --thread]          -number of threads to use to align equences (default is 4)
[-o || --oaligned]        -output file with aligned sequences
[-m || --tree_method]     -method to build a tree; options:  NJ, ML, ME (default is NJ)
[-l || --subst_model]     -amino acids substitution model (default is Jones-Taylor-Thornton (JTT) model)
	For NJ and ME trees  -l is one of the following: ["Jones-Taylor-Thornton (JTT) model", "No. of differences", "Equal input model", 
	"p-distance", "Poisson model", "Dayhoff model"]
	For ML tree: ["Jones-Taylor-Thornton (JTT) model", "Poisson model", "Equal input model", 
	"Dayhoff model", "Dayhoff model with Freqs. (F+)", "JTT with Freqs. (F+) model", "WAG model", 
	"WAG with Freqs. (F+) model", "LG model", "LG with Freqs. (F+) model", "General Reversible Mitochondrial (mtREV)", 
	"mtREV with Freqs. (F+) model", "General Reversible Chloroplast (cpREV)", "cpREV with Freqs. (F+) model",
	"General Reversible Transcriptase model (rtREV)", "rtREV with Freqs. (F+) model"]
[-g || --gaps_missing]    -how to treat gaps and missing data; one of the following: ["Complete deletion", "Partial deletion", "Pairwise deletion"]; defaultis "Complete deletion"
[-c || --coverage_cutoff] -Site Coverage Cutoff (%) if -g (||--gaps_missing) is set to "Partial deletion"
[-u || --cpu]             -number of threads to use for a tree building (default is 4)
[-p || --phylo]           -phylogeny test; for NJ and ME one of the following: ["None", "Bootstrap method", "Interior-branch test"];
	for ML: ["None", "Bootstrap method"]; default is "None"
[-b || --bootstrap]       -number of replicates for the bootstrap testing; if not provided the bootstrap test will not run
[-e || --initial_tree]    -initial tree for ML; on of the following: ["Make initial tree automatically (Default - NJ/BioNJ)", "Make initial tree automatically (Maximum parsimony)", "Make initial tree automatically (Neighbor joining)", 
	"Make initial tree automatically (BioNJ)"]; default is "Make initial tree automatically (Default - NJ/BioNJ)"
[-n || --subst_rate]      -amino acids substitution rate; on of the following: ["Uniform Rates", "Gamma Distributed (G)", "Has Invariant Sites (I)", "Gamma Distributed With Invariant Sites (G+I)"];
	default is "Uniform Rates"
[-x || --otree_params]    -output file with parameters for megacc tree building (extension is '.mao')
[-z || --otree]           -output file with the constuctred tree
'''


#Inputs
INPUT_FILE = "input.fa"
ALGORITHM = "--localpair"
REORDER_OR_NOT = ""
ALIGN_THREADS = 4

OUTPUT_FILE_FIRST = "aligned_proteins.fa"
OUTPUT_FILE_SECOND = None
OUTPUT_FILE_THIRD = "newTree"

#Parameters
LINUX_VERSION ="7160929-x86_64 Linux"
DATATYPE = "snProtein"
MISSING_SYMBOL = "?"
IDENTICAL_SYMBOL = "."
GAP_SYMBOL = "-"
NEW_SUBSECTION ="===================="
NOT_APPLICABLE = "Not Applicable"
DEFAULT_PATTERN_AMONG_LINEAGES = "Same (Homogeneous)"
ML_PHYLOGENY_TESTS = ["None", "Bootstrap method"]
NJ_ME_PHYLOGENY_TESTS = ["None", "Bootstrap method", "Interior-branch test"]
PHYLOGENY_TEST = "None"
BOOTSTRAPS = NOT_APPLICABLE
DEFAULT_ML_HEURISTIC_METHOD = "Nearest-Neighbor-Interchange (NNI)"
DEFAULT_CPU_NUMBER = "4"

RATES = ["Uniform Rates", "Gamma Distributed (G)", "Has Invariant Sites (I)", "Gamma Distributed With Invariant Sites (G+I)"]
NJ_ME_SUBSTITUTIAN_MODELS = ["Jones-Taylor-Thornton (JTT) model", "No. of differences", "Equal input model", 
"p-distance", "Poisson model", "Dayhoff model"]
ML_SUBSTITUTIAN_MODELS = ["Jones-Taylor-Thornton (JTT) model", "Poisson model", "Equal input model", 
"Dayhoff model", "Dayhoff model with Freqs. (F+)", "JTT with Freqs. (F+) model", "WAG model", 
"WAG with Freqs. (F+) model", "LG model", "LG with Freqs. (F+) model", "General Reversible Mitochondrial (mtREV)", 
"mtREV with Freqs. (F+) model", "General Reversible Chloroplast (cpREV)", "cpREV with Freqs. (F+) model",
"General Reversible Transcriptase model (rtREV)", "rtREV with Freqs. (F+) model"]
GAPS_AND_MISSING_DATA_BEHAVIOUR = ["Complete deletion", "Partial deletion", "Pairwise deletion"]
ML_INITIAL_TREE_OPTIONS = ["Make initial tree automatically (Default - NJ/BioNJ)", "Make initial tree automatically (Maximum parsimony)", "Make initial tree automatically (Neighbor joining)", 
"Make initial tree automatically (BioNJ)"]
SUBST_MODEL = NJ_ME_SUBSTITUTIAN_MODELS[0]
GAPS_MISSING = GAPS_AND_MISSING_DATA_BEHAVIOUR[0]
COVERAGE_CUTOFF = None
TREE_THREADS = 4
ML_INITIAL_TREE = ML_INITIAL_TREE_OPTIONS[0]
SUBST_RATE = RATES[0]

MAFFT_PROGRAM = None 
MEGACC_PROGRAM = None


TREE_METHODS = {"NJ": NJ_PARAMETERS, "ML": ML_PARAMETERS, "ME": ME_PARAMETERS}
TREE_METHOD = "NJ"

NJ_PARAMETERS = {
	"[ MEGAinfo ]":{
		"ver=": LINUX_VERSION
	},
	"[ DataSettings ]":{
		"datatype": DATATYPE,
		"MissingBaseSymbol": MISSING_SYMBOL,
		"IdenticalBaseSymbol": IDENTICAL_SYMBOL,
		"GapSymbol": GAP_SYMBOL
	},
	"[ ProcessTypes ]":{
		"ppInfer": "true",
		"ppNJ": "true"
	},
	"[ AnalysisSettings ]":{
		"Analysis": "Phylogeny Reconstruction",
		"Scope": "All Selected Taxa",
		"Statistical Method": "Neighbor-joining",
		"Phylogeny Test": NOT_APPLICABLE,
		"Test of Phylogeny": PHYLOGENY_TEST,
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
		"Site Coverage Cutoff (%)": NOT_APPLICABLE,
		"Has Time Limit": "False",
		"Maximum Execution Time": "-1"
	}
}

ML_PARAMETERS = {
	"[ MEGAinfo ]":{
		"ver=": LINUX_VERSION
	},
	"[ DataSettings ]":{
		"datatype": DATATYPE,
		"MissingBaseSymbol": MISSING_SYMBOL,
		"IdenticalBaseSymbol": IDENTICAL_SYMBOL,
		"GapSymbol": GAP_SYMBOL
	},
	"[ ProcessTypes ]":{
		"ppInfer":"true",
		"ppML":"true"
	},
	"[ AnalysisSettings ]":{
		"Analysis": "Phylogeny Reconstruction",
		"Scope": "All Selected Taxa",
		"Statistical Method": "Maximum Likelihood",
		"Phylogeny Test": NEW_SUBSECTION,
		"Test of Phylogeny": PHYLOGENY_TEST,
		"No. of Bootstrap Replications": BOOTSTRAPS,
		"Substitution Model": NEW_SUBSECTION,
		"Substitutions Type": "Amino acid",
		"Model/Method": SUBST_MODEL,
		"Rates and Patterns": NEW_SUBSECTION,
		"Rates among Sites": SUBST_RATE,
		"No of Discrete Gamma Categories": NOT_APPLICABLE,
		"Data Subset to Use": NEW_SUBSECTION,
		"Gaps/Missing Data Treatment": GAPS_MISSING,
		"Site Coverage Cutoff (%)": NOT_APPLICABLE,
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
		"datatype=": DATATYPE,
		"MissingBaseSymbol": MISSING_SYMBOL,
		"IdenticalBaseSymbol": IDENTICAL_SYMBOL,
		"GapSymbol": GAP_SYMBOL
	},
	"[ ProcessTypes ]":{
		"ppInfer": "true",
		"ppME": "true"
	},
	"[ AnalysisSettings ]":{
		"Analysis": "Phylogeny Reconstruction",
		"Scope": "All Selected Taxa",
		"Statistical Method": "Minimum Evolution method",
		"Phylogeny Test": NEW_SUBSECTION,
		"Test of Phylogeny": PHYLOGENY_TEST,
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
		"Site Coverage Cutoff (%)": NOT_APPLICABLE,
		"Tree Inference Options": NEW_SUBSECTION,
		"ME Heuristic Method": "Close-Neighbor-Interchange (CNI)",
		"Initial Tree for ME": "Obtain initial tree by Neighbor-Joining",
		"ME Search Level": "1",
		"Has Time Limit": "False",
		"Maximum Execution Time": "-1"
	}
}

def initialyze(argv):
	global INPUT_FILE, ALGORITHM, REORDER_OR_NOT, MAFFT_PROGRAM, MEGACC_PROGRAM, ALIGN_THREADS, OUTPUT_FILE_FIRST, TREE_METHOD, SUBST_MODEL, GAPS_MISSING, 
	COVERAGE_CUTOFF, TREE_THREADS, PHYLOGENY_TEST, BOOTSTRAPS, ML_INITIAL_TREE, SUBST_RATE, OUTPUT_FILE_SECOND, OUTPUT_FILE_THIRD
	try:
		opts, args = getopt.getopt(argv[1:],"hi:a:r:k:f:t:o:m:l:g:c:u:p:b:e:n:x:z:",
		["isequence=", "al_algorithm=", "reorder=", "megacc", "mafft=", "thread=", "oaligned=",  
		"tree_method=", "subst_model=", "gaps_missing=", "coverage_cutoff=", 
		"cpu=", "phylotest=" "bootstrap=", "initial_tree=", "subst_rate=", "otree_params", "otree="])
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
			INPUT_FILE = str(arg).strip()
		elif opt in ("-s", "--al_algorithm"):
			param = str(arg).strip()
			if param in ["--localpair", "--genafpair", "--globalpair", "--retree 2 --maxiterate 1000", "--retree 2 --maxiterate 0"]
				ALGORITHM = param
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
			if param in NJ_ME_SUBSTITUTIAN_MODELS or param in ML_SUBSTITUTIAN_MODELS:
				SUBST_MODEL = param
		elif opt in ("-g", "--gaps_missing"):
			param = str(arg).strip()
			if param in ["Complete deletion", "Partial deletion", "Pairwise deletion"]:
				GAPS_MISSING = param
		elif opt in ("-c", "--coverage_cutoff"):
			COVERAGE_CUTOFF = str(arg).strip()
		elif opt in ("-u", "--cpu"):
			TREE_THREADS = str(arg).strip()
		elif opt in ("-p", "--phylo"):
			param = str(arg).strip()
			if param in NJ_ME_PHYLOGENY_TESTS or parma in ML_PHYLOGENY_TESTS:
				PHYLOGENY_TEST = param
		elif opt in ("-b", "--bootstrap"):
			param = str(arg).strip()
			if len(param) > 0:
				BOOTSTRAPS = param
		elif opt in ("-e", "--initial_tree"):
			param = str(arg).strip()
			if param in ML_INITIAL_TREE_OPTIONS:
				ML_INITIAL_TREE = param
		elif opt in ("-n", "--subst_rate"):
			param = str(arg).strip()
			if param in RATES:
				SUBST_RATE = RATES[param] 
		elif opt in ("-x", "--otree_params"):
			OUTPUT_FILE_SECOND = str(arg).strip()   
		elif opt in ("-z", "--otree"):
			OUTPUT_FILE_THIRD = str(arg).strip()   

MAFFT_PROGRAM = None 
MEGACC_PROGRAM = None

def align_sequences():
	mafft = "mafft"
	if MAFFT_PROGRAM != None:
		mafft = MAFFT_PROGRAM
	runSubProcess(" ".join([mafft, ALGORITHM, REORDER_OR_NOT, "--thread", ALIGN_THREADS, INPUT_FILE, ">", OUTPUT_FILE_FIRST]))


def buildTree():
	megacc = "megacc"
	if MEGACC_PROGRAM != None:
		megacc = MEGACC_PROGRAM
	treMethod = TREE_METHODS[TREE_METHOD].copy()
	for paramName, paramValue in params:
		 treMethod[paramName] = paramValue
		 
	with open(OUTPUT_FILE_SECOND, "w") as paramOutputFile:
		for element, subelement in treMethod:
			paramOutputFile.write(element + "\n")
			for param, val in subelement:
				paramOutputFile.write("=".join(param, val) + "\n")
	runSubProcess(" ".join([megacc, "-a", OUTPUT_FILE_SECOND, "-d", OUTPUT_FILE_FIRST, "-o", OUTPUT_FILE_THIRD]))

def runSubProcess(command):
	try:
		subprocess.call(command, shell=True)
	except OSError, osError:
		print "osError " + osError
		print traceback.print_exc()









