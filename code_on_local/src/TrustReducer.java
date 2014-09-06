import java.io.IOException;
import java.util.*;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.util.*;

public class TrustReducer extends Reducer<IntWritable, NodeOrDouble, IntWritable, Node> {
	public void reduce(IntWritable key, Iterable<NodeOrDouble> values, Context context)	throws IOException, InterruptedException {
        
        HashMap<Integer, Double> NPR = new HashMap<Integer, Double>();
        HashMap<Integer, Double> outerMap = new HashMap<Integer, Double>();
        HashMap<Integer, Double> innerMap = new HashMap<Integer, Double>();
        HashMap<Integer, Double> PR = new HashMap<Integer, Double>();
        HashMap<Integer, Double> PPR = new HashMap<Integer, Double>();
        HashMap<Integer, Node> nodeMap = new HashMap<Integer, Node>();
        ArrayList<NodeOrDouble> localDoubles = new ArrayList<NodeOrDouble>();
        Long size = new Long(0);
        size = Long.parseLong(context.getConfiguration().get("size").toString());


        // intialize NPR
        for ( NodeOrDouble itr : values ) {
            if (itr.isNode()) {
                Node currNode = itr.getNode();
                nodeMap.put( currNode.nodeid, currNode );
                NPR.put( currNode.nodeid, 0.0 );
                innerMap.put( currNode.nodeid, 0.0 );
                outerMap.put( currNode.nodeid, 0.0 );
                PR.put( currNode.nodeid, currNode.getPageRank() );
                PPR.put( currNode.nodeid, currNode.getPageRank() );
            }
            else 
                localDoubles.add(new NodeOrDouble(itr));
        }

        System.out.println("blocked: " + key.get() + " doublesize " + localDoubles.size());
        System.out.println("blocked: " + key.get() + " nodesize " + nodeMap.keySet().size());


        // intialize both PR and NPR
        for (NodeOrDouble itr : localDoubles) {

            if (itr.isNode()) {
                System.out.println("Exception");
                System.out.println("dest: " + itr.getDestID());  
                System.out.println("src: " + itr.getSrcID());  
                System.out.println("isNode? " + itr.isNode());
                System.out.println("Node id: " + itr.getNode().nodeid);
                System.out.println("Node id: " + itr.getNode().nodeid);
            }

            // retrieve destination id
            int destid = itr.getDestID();
            int srcid = itr.getSrcID();
            double pr = 0;
            try{
                pr = itr.getDouble();
            }
            catch ( NullPointerException e ) {
                System.out.println("Exception");
                System.out.println("dest id here: " + destid);
                System.out.println("src id here: " + srcid);
                if (itr.isNode())
                    System.out.println("Node? " + itr.isNode());
            }
            int srcBlockid = Node.retrieveBlockID( srcid );

            // determine if this is a innter-block node or 
            // mass from outer block, then update outerMap
            if ( srcBlockid != key.get() ) {
                //double collectedPR = outerMap.get( destid );
                double collectedPR = 0.0;
                try { 
                    collectedPR = outerMap.get( destid );
                } catch ( NullPointerException e ) {
                    System.out.println("Exception");
                    System.out.println("double block id: " + Node.retrieveBlockID( destid ));
                    System.out.println("this block id: " + key.get());
                    //System.out.println("outMap : " + outerMap.keySet().toString());
                    System.out.println("dest : " + destid );
                }
                collectedPR += itr.getDouble();
                outerMap.put(destid, collectedPR);
            }
        }

       int count = 0;
        do {
            // first of all, inner block commnucation
            for ( int nid : nodeMap.keySet() ) {
                Node currNode = nodeMap.get( nid );
                double linkWeight = PR.get( nid ) / currNode.outgoingSize();
                for ( int dest : currNode.outgoing ) {
                    if ( !innerMap.containsKey( dest ) ) continue;                    

                    double tmpPR = innerMap.get( dest );
                    tmpPR += linkWeight;
                    innerMap.put( dest, tmpPR );
                }
            }

            // then gathering both inner and outer mass
            for ( int nid : nodeMap.keySet() ) {
                double currPR = innerMap.get( nid ) + outerMap.get( nid );
                //System.out.println("gathering both inner and outer " + "nid: " + nid + " currPR: " + currPR );            
                NPR.put( nid, currPR ); 
            }
       
            // copy all the valus from NPR to PR
            for ( int nid : nodeMap.keySet() ) {
                double tmpPR = NPR.get( nid );
                tmpPR = 0.85 * tmpPR + 0.15 / size;
                PR.put( nid, tmpPR );
                NPR.put( nid, 0.0 );
                innerMap.put( nid, 0.0 );
            }
      
            count ++;
        } while ( count < 5 );
	
        double sum = 0.0;
        for ( int nid : nodeMap.keySet() ) {
            Node currNode = nodeMap.get( nid );
            currNode.setPageRank( PR.get(nid) );
            sum += Math.abs( (PR.get(nid) - PPR.get(nid)) / PR.get(nid));
            context.write( new IntWritable( currNode.nodeid ), currNode );
        }
        System.out.println("Error is: " + sum);
        context.getCounter(pr_Counter.residualError).increment((long) sum * 1000000); 
	}
}
