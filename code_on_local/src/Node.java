import java.util.*;
import java.io.*;
import org.apache.hadoop.io.Writable;


// added some changes around variable "blockid"
public class Node implements Iterable<Integer>, Writable{
    int blockid;  // changed by hh548
    int nodeid;
    double pageRank;
    int[] outgoing;
    static int[] blockSegment = {10328, 20373, 30629, 40645, 50462, 60841, 70591, 80118, 90497, 100501, 110567, 120945, 130999, 140574, 150953, 161332, 171154, 181514, 191625, 202004, 212383, 222762, 232593, 242878, 252938, 263149, 273210, 283473, 293255, 303043, 313370, 323522, 333883, 343663, 353645, 363929, 374236, 384554, 394929, 404712, 414617, 424747, 434707, 444489, 454285, 464398, 474196, 484050, 493968, 503752, 514131, 524510, 534709, 545088, 555467, 565846, 576225, 586604, 596585, 606367, 616148, 626448, 636240, 646022, 655804, 665666, 675448, 685230};


    //Here for internal Hadoop purposes only. Don't use this constructor!
    public Node() {
	    nodeid = -1;
	    outgoing = new int[0];
        blockid = -1;
        pageRank = 0;
    }



    //Construct a node with no outgoing links.
    public Node(int nid, int bid) {
	    nodeid = nid;
        blockid = bid;  // changed by hh548
	    outgoing = new int[0];    
        pageRank = 0;
    }



    //Construct a node where the outgoing links are have nodeids in outs.
    public Node(int nid, int bid ,int[] outs) {
        nodeid = nid;
        blockid = bid;  // changed by hh548
	    outgoing = outs;
        pageRank = 0;
    }



    // retrieve block ID given a node ID
    public static int retrieveBlockID( int nid ) {
        for (int i = 0; i < blockSegment.length; i++) {
            if (nid < blockSegment[i])
                return i;
        }
        return 68;
    }



    //Allow iteration through the outgoing edges.
    //Used for for-each loops
    public Iterator<Integer> iterator() {
	    ArrayList<Integer> al = new ArrayList<Integer>();
	    for(int i : outgoing) {
	        al.add(i);
	    }
	    return al.iterator();
    }
   

 
    //Get the number of outgoing edges
    public int outgoingSize() {
	    return outgoing.length;
    }


    
    //Set the outgoing edges to be a new array
    public void setOutgoing(int[] outs) {
	    outgoing = outs;
    }


    
    //Get the PageRank of this node.
    public double getPageRank() {
	    return pageRank;
    }



    public int getBlockID() {
        return blockid;
    }


    
    //Set the PageRank of this node
    public void setPageRank(double pr) {
	    pageRank = pr;
    }



    //Used for internal Hadoop purposes.
    //Describes how to write this node across a network
    public void write(DataOutput out) throws IOException {
	    out.writeInt(nodeid);
	    out.writeDouble(pageRank);
	    for(int n : outgoing) {
	        out.writeInt(n);
	    }
        out.writeInt(-1);
        out.writeInt(blockid);   // chaged by hh548
    }



    //Used for internal Hadoop purposes
    //Describes how to read this node from across a network
    public void readFields(DataInput in) throws IOException {
	    nodeid = in.readInt();
	    pageRank = in.readDouble();
	    int next = in.readInt();
	    ArrayList<Integer> ins = new ArrayList<Integer>();
	    while (next >= 0) {
	        ins.add(next);
	        next = in.readInt();
	    }

        blockid = in.readInt(); // changed by hh548
	    outgoing = new int[ins.size()];
	    for(int i = 0; i < ins.size(); i++) {
	        outgoing[i] = ins.get(i);
	    }
    }


    
    //Gives a human-readable representaton of the node.
    public String toString() {
	    String retv = "";
	    retv += pageRank + "\t";
	    String out = "";
	    for(int n : outgoing) out += "" + n + ",";
	    if(!out.equals("")) out = out.substring(0, out.length() - 1);
        if (out.equals("")) out = "#";
	    retv += out + "\t";
        retv += blockid;
	    return retv;
    }
}
