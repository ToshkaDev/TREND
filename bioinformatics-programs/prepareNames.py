#!/usr/bin/python
import sys, getopt

INPUT_FILE = None
OUTPUT_FILE = None


USAGE = "\n\nThe script makes sequence names in the file newick friendly.\n\n" + \
	"python 	" + sys.argv[0] + '''
	-h                     - help
	-i || --ifile          - input file
	-o || --ofile          - output file'''

def initialize(argv):
	global INPUT_FILE, OUTPUT_FILE
	try:
		opts, args = getopt.getopt(argv[1:],"hi:o:",["ifile=", "ofile="])
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
	except Exception as e:
		print "===========ERROR==========\n " + str(e) + USAGE
		sys.exit(2)
	

def prepareNames():
	with open(OUTPUT_FILE, "w") as outputFile:
		with open(INPUT_FILE, "r") as inputFile:
			for line in inputFile:
				line = line.replace('(','_').replace(')','_').replace(':','_').replace(',','_').replace("'", "").replace("/","").replace(" ","_")
				outputFile.write(line)


def main(argv):
	initialize(argv)
	prepareNames()
	

if __name__ == "__main__":
	main(sys.argv)
	
