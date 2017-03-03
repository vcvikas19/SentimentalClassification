package com.vikas.tweetopennlpHadoop;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

public class Reduce1 extends MapReduceBase implements Reducer<Text, IntWritable, Text, IntWritable> {
    /**
     * Count the frequency of each classified text group
     */

    @Override
    public void reduce(Text key, java.util.Iterator<IntWritable>classifiedText,OutputCollector<Text, IntWritable> output, Reporter reporter)
            throws IOException {
        int sum = 0;
        while (classifiedText.hasNext()) {
            sum += classifiedText.next().get(); //Sum the frequency
        }
        output.collect(key, new IntWritable(sum));
    }

	
}