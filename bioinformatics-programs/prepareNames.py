#!/usr/bin/python
import sys, getopt
import re
from ete2 import Tree

INPUT_FILE = None
OUTPUT_FILE = None
INPUT_FILE_TREE = None
OUTPUT_FILE_TREE = None


USAGE = "\n\nThe script makes sequence names in the file newick friendly.\n\n" + \
	"python 	" + sys.argv[0] + '''
	-h                     - help
	-i || --ifile          - input file with sequences
	-s || --sfile          - input file with phylogenetic tree in newick format
	-o || --ofile          - output file with sequences with changed names
	-n || --nfile          - output file with tree with changed names of leaves
	'''

def initialize(argv):
	global INPUT_FILE, INPUT_FILE_TREE, OUTPUT_FILE, OUTPUT_FILE_TREE
	try:
		opts, args = getopt.getopt(argv[1:],"hi:s:o:n:",["ifile=", "sfile=", "ofile=", "nfile="])
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
				INPUT_FILE = str(arg).strip()
			elif opt in ("-o", "--ofile"):
				OUTPUT_FILE = str(arg).strip()
			elif opt in ("-s", "--sfile"):
				INPUT_FILE_TREE = str(arg).strip()
			elif opt in ("-n", "--nfile"):
				OUTPUT_FILE_TREE = str(arg).strip()
	except Exception as e:
		print "===========ERROR==========\n " + str(e) + USAGE
		sys.exit(2)

REGEX_VOID = re.compile(r"(\(|\)|:|,|}|{|'|/|]|\[|\\)")
REGEX_UNDERSCORE = re.compile(r"( |\|)")
REGEX_VOID_SUBST = ""
REGEX_UNDERSCORE_SUBST = "_"
REGEX_LEAF_NAME_1 = re.compile(r"(\('[^\(].+?':|,'[^\(].+?':)")
REGEX_LEAF_NAME_2 = re.compile(r"(\([^\(].+?:|,[^\(].+?:)")


def getChangedName(line):
	line = REGEX_VOID.sub(REGEX_VOID_SUBST, line)
	return REGEX_UNDERSCORE.sub(REGEX_UNDERSCORE_SUBST, line)

def getChangedNameForTree():
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

def prepareNames():
	if not INPUT_FILE or not OUTPUT_FILE:
		print "Both input file name with sequences and output file name should be provided!"
		return
	else:
		print "Changing sequence names"
	with open(OUTPUT_FILE, "w") as outputFile:
		with open(INPUT_FILE, "r") as inputFile:
			for line in inputFile:
				outputFile.write(getChangedName(line))

def main(argv):
	initialize(argv)
	prepareNames()
	getChangedNameForTree()


if __name__ == "__main__":
	main(sys.argv)
