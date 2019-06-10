#!/usr/bin/python
import sys
import getopt
from subprocess import call


USAGE = "\nThis script \n" + \
"\n\n" + "python" + sys.argv[0] + '''

-i || --ifile              -input file  
-o || --ofile              -output file redunduncy decreased sequences
[-c || --clust_identity]   -identity level for redunduncy reduction
[-u || --cpu]              -number of threads to use in cd-hit run
[-A || --cdhit_path]       -full path to cd-hit
[-m || --cdhit_memory]     -memory allocated for cd-hit 
'''

# ful path to cd-hit
CD_HIT = "/usr/local/bin/cd-hit"
INPUT_FILE = None
OUTPUT_FILE = None
CD_HIT_CLUSTER_IDENTITY = 0.95
NUMBER_OF_THREADS = 4
# in MB
CD_HIT_MEMORY = 2000
# cd-hit word length for clustering
WORD_LENGTH=5
#6 4
#5 3
#4 2
#3 1
def initialyze(argv):
	global CD_HIT, INPUT_FILE, OUTPUT_FILE, CD_HIT_CLUSTER_IDENTITY, WORD_LENGTH, NUMBER_OF_THREADS, CD_HIT_MEMORY
	try:
		opts, args = getopt.getopt(argv[1:],"hi:o:c:u:A:m:",["ifile=", "ofile=", "clust_identity=", "cpu=", "cdhit_path=", "cdhit_memory="])
		if len(opts) == 0:
			raise getopt.GetoptError("Options are required\n")
	except getopt.GetoptError as e:
		print "===========ERROR==========\n " + str(e) + USAGE
		sys.exit(2)
	for opt, arg in opts:
		if opt == '-h':
			print USAGE
			sys.exit()
		elif opt in ("-i", "--ifile"):
			INPUT_FILE = str(arg).strip()
		elif opt in ("-o", "--ofile"):
			OUTPUT_FILE = str(arg).strip()
		elif opt in ("-c", "--clust_identity"):
			CD_HIT_CLUSTER_IDENTITY = float(arg)
		elif opt in ("-u", "--cpu"):
			NUMBER_OF_THREADS = int(arg)
		elif opt in ("-A", "--cdhit_path"):
			CD_HIT = str(arg).strip()
		elif opt in ("-m", "--cdhit_memory"):
			CD_HIT_MEMORY = int(arg)
	if float(CD_HIT_CLUSTER_IDENTITY) < 0.4:	
		print "Too low clustering identity. Exiting!"
		sys.exit(2)
	elif float(CD_HIT_CLUSTER_IDENTITY) <= 0.5:
		WORD_LENGTH = 2
	elif float(CD_HIT_CLUSTER_IDENTITY) <= 0.55:
		WORD_LENGTH = 3
	elif float(CD_HIT_CLUSTER_IDENTITY) <= 0.65:
		WORD_LENGTH = 4

		
def cdHit(): 
	cd_hit_commandline = '{0} -i {1} -d 0 -o {2} -c {3} -n {4} -G 1 -g 1 -b 20 -s 0.0 -aL 0.0 -aS 0.0 -T {5} -M {6}'.format(CD_HIT, INPUT_FILE, \
	OUTPUT_FILE, CD_HIT_CLUSTER_IDENTITY, WORD_LENGTH, NUMBER_OF_THREADS, CD_HIT_MEMORY)
	print(cd_hit_commandline)
	runSubProcess(cd_hit_commandline)
	
def runSubProcess(command):
	try:
		call(command, shell=True)
	except OSError, osError:
		print "osError " + osError
		print traceback.print_exc()
			
			
def main(argv):
	initialyze(argv)
	cdHit()

if __name__ == "__main__":
	main(sys.argv)
		
