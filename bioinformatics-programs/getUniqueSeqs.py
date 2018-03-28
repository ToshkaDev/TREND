#!/usr/bin/python

import sys, fileinput
from Bio import SeqIO

#usage: 

#input1: 


fileWithSeq = sys.argv[1]
seqNames = set()

currentFile = open(fileWithSeq, "r")
for record in SeqIO.parse(currentFile, "fasta"):
	if record.description not in seqNames:
		print ">" + record.description
		print  record.seq
		seqNames.add(record.description)
currentFile.close()
			
		




