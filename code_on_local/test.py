import sys

f = open(sys.argv[1], 'r')
line = f.readline()
fw = open(sys.argv[2], 'w')

while line:
    nid, val, _, __ = line.split()
    fw.write(''.join([nid, ' ', val, '\n']))
    line = f.readline()
    
f.close()
fw.close()
