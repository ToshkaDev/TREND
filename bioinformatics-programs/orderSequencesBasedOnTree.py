#!/usr/bin/python
import sys, getopt, json, collections, re
import copy
from Bio import Phylo, SeqIO
from ete2 import Tree, SeqMotifFace, TreeStyle, add_face_to_node

USAGE = "\nThis script enumerates protein sequence names on the provided phylogenetic tree and in the file with aligned proteins\n" + \
"It additionally saves phylogenetic tree with changed protein names in newick format\n\n" + "python" + sys.argv[0] + '''

[-s || --ialigned]         -input file with aligned sequences
-d || --itree              -input file with phylogenetic tree  
[-o || --oaligned]         -output file with aligned sequences with changed protein names
[-b || --othird]           -output file with prepared tree with changed protein and no domains in newick format
'''

# Specified via program arguments
SEQS_ALIGNED = None          	                     #file with aligned sequences
TREE_FILE = None    			                     #file with tree
OUTPUT_ALIGNED_FILENAME = "alignerProteins.fa"       #file with aligned sequences with changed protein names
OUTPUT_TREE_NEWICK_FILENAME = "newTree.newick"       #file with prepared tree with in newick format

# Datastructures which are get initialized and manipulated by the program
ALIGNED_PROTEIN_NAME_TO_SEQ = dict()
ALIGNED_NAMES_TO_PROCESSED = dict()
PROCESSED_TO_ALIGNED_NAMES = dict()

def initialyze(argv):
	global SEQS_ALIGNED, TREE_FILE, OUTPUT_ALIGNED_FILENAME, OUTPUT_TREE_NEWICK_FILENAME
	try:
		opts, args = getopt.getopt(argv[1:],"hs:d:o:b:",["ialigned=", "itree=", "oaligned=", "otree="])
		if len(opts) == 0:
			raise getopt.GetoptError("Options are required\n")
	except getopt.GetoptError as e:
		print "===========ERROR==========\n " + str(e) + USAGE
		sys.exit(2)
	for opt, arg in opts:
		if opt == '-h':
			print USAGE
			sys.exit()
		elif opt in ("-s", "--ialigned"):
			SEQS_ALIGNED = str(arg).strip()
		elif opt in ("-d", "--itree"):
			TREE_FILE = str(arg).strip()
		elif opt in ("-o", "--oaligned"):
			OUTPUT_ALIGNED_FILENAME = str(arg).strip()
		elif opt in ("-b", "--otree"):
			OUTPUT_TREE_NEWICK_FILENAME = str(arg).strip()
								
def processFileWithSeqs():		
	with open(SEQS_ALIGNED, "r") as alignedSeqs:
		for sequence in SeqIO.parse(alignedSeqs, "fasta"):
			ALIGNED_PROTEIN_NAME_TO_SEQ[sequence.description.strip()] = str(sequence.seq)                   #

def writeSeqsAndTree():
	prepareNameDict()
	tree = Tree(TREE_FILE)
	terminals = tree.get_leaves()
	# Change protein names in datas sctrucuters and write protein sequences with changed names to file 					
	with open(OUTPUT_ALIGNED_FILENAME, "w") as outputFile:
		for i in xrange(len(terminals)):
			proteinName = terminals[i].name.strip("'")
			processedName = prepareName(proteinName)
			if processedName in PROCESSED_TO_ALIGNED_NAMES:
				terminals[i].name = str(i+1) + "_" + proteinName
				alignedName = PROCESSED_TO_ALIGNED_NAMES[processedName]					
				ALIGNED_PROTEIN_NAME_TO_SEQ[terminals[i].name] = ALIGNED_PROTEIN_NAME_TO_SEQ[alignedName]
				del ALIGNED_PROTEIN_NAME_TO_SEQ[alignedName]
				outputFile.write(">" + terminals[i].name + "\n")
				outputFile.write(str(ALIGNED_PROTEIN_NAME_TO_SEQ[terminals[i].name]) + "\n")		
	tree.write(outfile=OUTPUT_TREE_NEWICK_FILENAME)

def prepareNameDict():
	for protein in ALIGNED_PROTEIN_NAME_TO_SEQ:
		processdName = prepareName(protein)		
		PROCESSED_TO_ALIGNED_NAMES[processdName] = protein		
		
REGEX_UNDERSCORE = re.compile(r"(\W|_)")
REGEX_UNDERSCORE_SUBST = ""

def prepareName(line):
	return REGEX_UNDERSCORE.sub(REGEX_UNDERSCORE_SUBST, line)
	
def main(argv):
	initialyze(argv)
	processFileWithSeqs()
	writeSeqsAndTree()

if __name__ == "__main__":
	main(sys.argv)
		
