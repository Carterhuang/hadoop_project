import sys
import os

print 'first argment: nodes.txt'
print 'second argment: edges.txt'
print 'third argment: blocks.txt'

## determine total number of nodes
cmd = "tail -n 1 " + sys.argv[1]
numOfNodes = float(os.popen(cmd).read().split()[0].strip())
initPR = 1.0 / numOfNodes

## determine different blocks for different nodes
fblock = open(sys.argv[3], 'r')
block_lst = fblock.read().split('\n')
block_lst.pop()
block_lst = [ int(num) for num in block_lst ]
fblock.close()
print block_lst

fwrite = open(sys.argv[4], 'w')

## read edges.txt
with open(sys.argv[2], 'r') as fread:

    curr_src_index = 0
    ## this array is for the dest list of each node
    destList = []
    ## this is a string buffer to be written to output file
    outputBlock = ''
    ## current block
    currBlock = 0

    line = fread.readline()
    while line != '':

        src, dest, rd = line.split()
        src = int(src)
        rd = float(src)
       
        ## if not matching curr_src_index, write buffer to output file 
        ## and increment curr_index_src   
        if (src != curr_src_index):

            if len(destList) > 0:
                ## remove last comma
                destList.pop()

            ## a special case for left ove node, if no outgoing link, output '#'
            if len(destList) == 0:
                destList = ['#']
           
            outputBlock = ''.join([str(curr_src_index), '\t', str(initPR), '\t', ''.join(destList), '\t', str(currBlock), '\n'])
 
            if (src >= block_lst[currBlock]):
                currBlock = currBlock + 1        
            
            ## clear dest list
            destList = []
            fwrite.write(outputBlock)
            curr_src_index = curr_src_index + 1
            continue;

        ##if rd < 0.452 or rd > 0.462:
        if rd < 0.579 or rd > 0.589:
            destList.append(dest)
            destList.append(',')
         
        line = fread.readline()



if len(destList) > 0: destList.pop()
if len(destList) == 0: destList = ['#']
outputBlock = ''.join([str(curr_src_index), '\t', str(initPR), '\t', ''.join(destList), '\t', str(currBlock),'\n'])
fwrite.write(outputBlock)

fread.close()
fwrite.close()
                          
