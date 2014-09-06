import sys

f1 = open(sys.argv[1], 'r')
f2 = open(sys.argv[2], 'r')
    
line1 = f1.readline()
line2 = f2.readline()

d1 = {}
d2 = {}

mysum = 0
counter = 0

while line1:
    nid1, val1, _, __ = line1.split()
    nid2, val2, _, __ = line2.split()
    val1 = float(val1)
    val2 = float(val2)
    nid1 = int(nid1)
    nid2 = int(nid2)
    d1[nid1] = val1
    d2[nid2] = val2
    line1 = f1.readline()
    line2 = f2.readline()
    counter += 1

f1.close()
f2.close()

for k in d1.keys():
    mysum += abs(( d1[k] - d2[k] ) / d2[k])


print 'counter: ', counter
print 'sum: ', mysum
print 'residual', mysum / counter
