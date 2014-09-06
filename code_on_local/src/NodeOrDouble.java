import org.apache.hadoop.io.Writable;
import java.io.*;

// Node + Integer (Either Node Integer, in Haskell)
public class NodeOrDouble implements Writable{
    private Node n;
    private Double d;
    private int srcid;          // changed by hh548
    private int destid;           // changed by hh548
    private boolean is_node;

    //Used for internal Hadoop purposes only. 
    //Do not use this constructor!
    public NodeOrDouble() {
	    this.is_node = false;
    }


    public NodeOrDouble( NodeOrDouble n) {
        this.srcid = n.getSrcID();
        this.destid = n.getDestID();
        this.d = n.getDouble();
        this.is_node = n.isNode();
    }


    //Construct a NodeOrDouble that is a node.
    public NodeOrDouble(Node n) {
	    this.n = n;
	    this.is_node = true;
    }
    
    //Construct a NodeOrDouble that is a Double, which is also the mass
    //to be passed to other nodes
    public NodeOrDouble(int src, int dest, Double d) {
	    this.d = d;
	    this.is_node = false;
        this.srcid = src;
        this.destid = dest;
    }

    //Find out whether this is actually a Node or not
    //If not, it's a Double
    public boolean isNode() {
	    return is_node;
    }

    //If this is a Node, return it.
    //Otherwise, return null
    public Node getNode() {
	    if(!isNode()) return null;
	    return n;
    }
    
    //If this is a Double, return it.
    //Otherwise, return null
    public Double getDouble() {
	    if(isNode()) return null;
	    return d;
    }


    // return source ID
    public int getSrcID() {
        if(isNode()) return -2;
        return srcid;
    }


    //If this is a Double, return destidID
    public int getDestID() {
        if(isNode()) return -5;
        return destid;
    }
    


    //Used for internal Hadoop purposes only
    //Describes how to write NodeOrDouble objects across a network
    public void write(DataOutput out) throws IOException {
	    out.writeBoolean(is_node);
	    if(is_node) {
	        n.write(out);
	    }
	    else {
            out.writeInt(srcid);
            out.writeInt(destid);
	        out.writeDouble(d);
	    }
    }

    //Used for internal Hadoop purposes only
    //Describes how to read NodeOrDouble objects from across a network
    public void readFields(DataInput in) throws IOException {
	    is_node = in.readBoolean();
	    if(is_node) {
	        n = new Node(-2, -2); //just to avoid errors --- wish this was static
	        n.readFields(in);
	    } else {
            srcid = in.readInt();
            destid = in.readInt();
	        d = in.readDouble();
	    }
    }
}
