package com.vikas.tweetopennlpHadoop;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DocumentSampleStream;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;


public class Map1 extends MapReduceBase implements Mapper<LongWritable, Text, Text, IntWritable> {
    private final static IntWritable one = new IntWritable(1);
    private Text word = new Text();
    static DoccatModel model;
    static int TP=0;
    static int FN=0;
    static int TN=0;
    static int FP=0;
    static long p=0;
	static long n =0;
    
    /**
     * Mapper which reads Tweets text file Store 
     * as <"Positive",1> or <"Negative",1>
     */
    public static int flag =0;
    public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter)
            throws IOException {
        String line = value.toString();//streaming each tweet from the text file
        if(flag==0)
        {
        	String d=  trainModel1();
            System.out.println(d);
            flag++;
        }
      
        if (line != null) {
        	//System.out.println(line);
           word.set(classifyNewTweet(line));
          //  System.out.println(line);//invoke classify class to get tweet group of each text
            output.collect(word, one);
        } else {
            word.set("Error");
            output.collect(word, one);//Key,value for Mapper
        }
    }
    public String trainModel1() {
		InputStream dataIn = null;
		try {
			dataIn = new FileInputStream("/home/vikas/dst/t/NLP/big/50tr");
			ObjectStream lineStream = new PlainTextByLineStream(dataIn, "UTF-8");
			ObjectStream sampleStream = new DocumentSampleStream(lineStream);
		//	Configuration conf = new Configuration();
	       // conf.addResource(new Path("/usr/local/hadoop/etc/hadoop/core-site.xml"));
	       // conf.addResource(new Path("/usr/local/hadoop/etc/hadoop/hdfs-site.xml"));
	       // FileSystem fs= FileSystem.get(conf);
	      //  Path path = new Path("hdfs://localhost:54310/1/60/train");
	      //  FSDataInputStream in = fs.open(path);
		//	ObjectStream lineStream = new PlainTextByLineStream(in, "UTF-8");
		//	ObjectStream sampleStream = new DocumentSampleStream(lineStream);
			// Specifies the minimum number of times a feature must be seen
			int cutoff = 2;
			int trainingIterations = 30;
			model = DocumentCategorizerME.train("en", sampleStream, cutoff,
					trainingIterations);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (dataIn != null) {
				try {
					dataIn.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return "done";
	}
    
    
    public String classifyNewTweet(String tweet) {
    	String[] arr = tweet.split(",");
		DocumentCategorizerME myCategorizer = new DocumentCategorizerME(model);
		double[] outcomes = myCategorizer.categorize(arr[1]);
		String category = myCategorizer.getBestCategory(outcomes);
		if (category.equalsIgnoreCase("1")) {
			System.out.println(tweet);
			System.out.println("The tweet is positive :) ");
			p++;
			if(arr[0].equals("1"))
			{
				TP++;
			}
			if(arr[0].equals("0"))
			{
				FP++;
			}
			
			return "The tweet is positive :) ";
		} else {
			System.out.println(tweet);
			System.out.println("The tweet is negative :( ");
			n++;
			if(arr[0].equals("0"))
			{
				TN++;
			}
			if(arr[0].equals("1"))
			{
				FN++;
			}
			
			
			return "The tweet is negative :( ";
		}
	}
    
    
    
}