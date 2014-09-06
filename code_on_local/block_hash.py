# Fri May  9 12:11:33 EDT 2014
import sys
import random
print 'USAGE:', 'python blockhash.py <path to nodes.txt> <output filepath>'

fo = open(sys.argv[2], "wb")

bins = []
for i in range(0, 68): bins.append([])

for line in open(sys.argv[1], 'rb'):
    lst = line.split()
    binnum = random.randint(0, 67)
    bins[binnum].append(int(lst[0]))
bucketnum = 0

for b in bins:
    print bucketnum
    for nid in b:
        tmp = ''.join([ str(nid), '\t', str(bucketnum), '\n'])
        fo.write(tmp)
    bucketnum += 1

fo.close()
