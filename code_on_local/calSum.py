import sys

f = open(sys.argv[1], 'r')
line = f.readline()
sum = 0
while line:
    nid, val, _, __ = line.split()
    val = float(val)
    sum += val
    line = f.readline()

print 'sum of all values', sum
