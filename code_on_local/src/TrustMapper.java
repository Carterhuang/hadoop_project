import java.io.IOException;
import java.util.*;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.util.*;

public class TrustMapper extends Mapper<IntWritable, Node, IntWritable, NodeOrDouble> {
    // Note: now key is blockid
	public void map(IntWritable key, Node value, Context context) throws IOException, InterruptedException {
    
        int nextID = -1;
		double p = 0;
        int c = 100000;
        // every time a trustmapper is called, counter.increment is triggered
        // in this way we count the total number of nodes
		context.getCounter(pr_Counter.size).increment(1);
		if (value.outgoingSize() == 0) {
			//long inc = (long) (c * value.getPageRank());
			//context.getCounter(pr_Counter.leftover).increment(inc);
		} else {
            p = value.getPageRank() / value.outgoingSize();
        }

        // This line of code is for preserving node in the context
        //if (key.get() != Node.retrieveBlockID( value.nodeid ))
        //    System.out.println("!!!!!!!!!!!!!!!!");

        int bid = Node.retrieveBlockID(value.nodeid); 
		context.write( new IntWritable( bid ) , new NodeOrDouble(value));

		Iterator<Integer> itr = value.iterator();
		while (itr.hasNext()) {
			nextID = itr.next();
			// Pass PageRank mass to neighbors
            int tmpBlockID = Node.retrieveBlockID( nextID );
            // Using block ID and a <destNodeID, mass> pair
            if (nextID < 0) {
                System.out.println("nodeid " + value.nodeid);
                System.out.println("nextID " + nextID);
            }
			context.write(new IntWritable( tmpBlockID ),new NodeOrDouble( value.nodeid, nextID, p ));
		}
	}
}

