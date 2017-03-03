package com.vikas.h;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;


public class Map extends MapReduceBase implements Mapper<LongWritable, Text, Text, IntWritable> {
    private final static IntWritable one = new IntWritable(1);
    private Text word = new Text();
    Classify classify = new Classify();

    /**
     * Mapper which reads Tweets text file Store 
     * as <"Positive",1> or <"Negative",1>
     */
    
    public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter)
            throws IOException {
        String line = value.toString();//streaming each tweet from the text file
        if (line != null) {
            word.set(classify.classify(line));
          //  System.out.println(line);//invoke classify class to get tweet group of each text
            output.collect(word, one);
        } else {
            word.set("Error");
            output.collect(word, one);//Key,value for Mapper
        }
    }
}