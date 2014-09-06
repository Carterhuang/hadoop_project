import java.io.IOException;
import java.util.*;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.*;
import org.apache.hadoop.mapreduce.lib.output.*;
import org.apache.hadoop.util.*;


public class PageRank {

    static final long totalsize = 685230;
	public static void main(String[] args) throws IOException {
		int numRepititions = 10 ; //The number of PageRank passes to run
		long leftover = 0; //How much PageRank mass did not get moved to any node
		long size = 0; // The size of the internet graph
		for(int i = 0; i < numRepititions; i++) { //We need to run 2 iterations to make 1 pass
			Job job;
			//Run the right job for the current pass
			job = getTrustJob();

			String inputPath = i == 0 ? "input" : "stage" + (i-1);
			String outputPath = "stage" + i;

			FileInputFormat.addInputPath(job, new Path(inputPath));
			FileOutputFormat.setOutputPath(job, new Path(outputPath));

			try { 
                System.out.println("Executing here?");
				job.waitForCompletion(true); //run the job
			} catch(Exception e) {
				System.err.println("ERROR IN JOB: " + e);
				return;
			}

            Counters counters = job.getCounters();
            double error = counters.findCounter(pr_Counter.residualError).getValue() / 1000000.0/ totalsize;
            if (error < 0.001) {
                System.out.println("Algorithm converges");
                System.out.println("Residual Error is: " + error);
                break;
            }
	}
	}


	public static Job getStandardJob(String l, String s) throws IOException {
		Configuration conf = new Configuration();
        conf.set("size", ""+totalsize);
        conf.set("residualError", ""+0);

		Job job = new Job(conf);

		job.setOutputKeyClass(IntWritable.class); //We output <Int, Node> pairs
		job.setOutputValueClass(Node.class);

		job.setInputFormatClass(NodeInputFormat.class); //We take in <Int,Node> pairs
		job.setOutputFormatClass(TextOutputFormat.class);

		job.setJarByClass(PageRank.class); //The current jar we're in

		return job;
	}

	public static Job getTrustJob() throws IOException{

		Job job = getStandardJob("", ""); //We don't need any extra variables

		job.setMapOutputKeyClass(IntWritable.class); //Our mapper puts out something different than our reducer
		job.setMapOutputValueClass(NodeOrDouble.class); //in particular, we output <Int, Node+Double> pairs
    
		job.setMapperClass(TrustMapper.class);
		job.setReducerClass(TrustReducer.class);

		return job;
	}
}



