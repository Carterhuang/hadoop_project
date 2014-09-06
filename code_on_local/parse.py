import sys

f = open(sys.argv[1], 'r')
line = f.readline()
lst = []

while line:
    block = int(line)
    lst.append(block)
    line = f.readline()
print 'size', len(lst)
print lst
