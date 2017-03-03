package com.vikas.LMCHadoop;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

import com.aliasi.classify.Classification;
import com.aliasi.classify.Classified;
import com.aliasi.classify.ConditionalClassification;
import com.aliasi.classify.DynamicLMClassifier;
import com.aliasi.classify.LMClassifier;
import com.aliasi.corpus.ObjectHandler;
import com.aliasi.util.AbstractExternalizable;
import com.aliasi.util.Compilable;
import com.aliasi.util.Files;
import com.vikas.LMC.train;


public class Map extends MapReduceBase implements Mapper<LongWritable, Text, Text, IntWritable> {
    private final static IntWritable one = new IntWritable(1);
    private Text word = new Text();
  //  Classify classify = new Classify();
  public static int flag=0;
    /**
     * Mapper which reads Tweets text file Store 
     * as <"Positive",1> or <"Negative",1>
     */
  static int TP=0;
  static int FN=0;
  static int TN=0;
  static int FP=0;
  static long p=0;
	static long n =0;
  String[] categories;
//public static int flag =0;
	@SuppressWarnings("rawtypes")
	LMClassifier lmc;
    public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter)
            throws IOException {
        
        if(flag==0)
        {
        	try {
				ReadObjectHadoop ();
				flag++;
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
        String line = value.toString();//streaming each tweet from the text file
        if (line != null) {
            try {
				word.set(classify(line));
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
          //  System.out.println(line);//invoke classify class to get tweet group of each text
            output.collect(word, one);
        } else {
            word.set("Error");
            output.collect(word, one);//Key,value for Mapper
        }
    }
    
    public void ReadObjectHadoop ()throws IOException, ClassNotFoundException
    {
    	//Configuration conf = new Configuration();
       // conf.addResource(new Path("/usr/local/hadoop/etc/hadoop/core-site.xml"));
       // conf.addResource(new Path("/usr/local/hadoop/etc/hadoop/hdfs-site.xml"));
      lmc = (LMClassifier) AbstractExternalizable.readObject(new File("/home/vikas/dst/t/LMC/big/training/50/classifier50mr.txt"));
       categories = lmc.categories();
      //  Path path = new Path("hdfs://localhost:54310/LMC/classifier10.txt");
     	//FileSystem fs = FileSystem.get(conf);
		//InputStream in = fs.open(path);
		//ObjectInputStream objReader = new ObjectInputStream(in);
        //lmc = (LMClassifier)	objReader.readObject();
		//categories = lmc.categories();
    }
    public String classify(String sCurrentLine) throws IOException, ClassNotFoundException {
    	String[] arr = sCurrentLine.split(",");
		ConditionalClassification classification = lmc.classify(arr[1]);
		System.out.println(arr[1]+"******"+classification.bestCategory());
		if(classification.bestCategory().equals("pos"))
		{p++;
		if(arr[0].equals("1"))
		{
			TP++;
		}
		if(arr[0].equals("0"))
		{
			FP++;
		}
		
		
		
		}
		else
		{
			n++;
			if(arr[0].equals("0"))
			{
				TN++;
			}
			if(arr[0].equals("1"))
			{
				FN++;
			}
			
			
			
		}
		
		
		return classification.bestCategory();	
	}  
    
    
}