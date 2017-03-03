package com.vikas.LMCHadoop;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;

import com.aliasi.classify.Classification;
import com.aliasi.classify.Classified;
import com.aliasi.classify.DynamicLMClassifier;
import com.aliasi.classify.LMClassifier;
import com.aliasi.corpus.ObjectHandler;
import com.aliasi.util.AbstractExternalizable;
import com.aliasi.util.Compilable;
import com.aliasi.util.Files;
import com.vikas.tweetopennlpHadoop.Map1;

public class RunMe {
	/**
	 * Remove url from the string using regular expression
	 * 
	 * @param text
	 * @return URL free text
	 */
	 public static long startTime; 
		public static long endTime;
		//public static long startTime;  
	private static String removeUrl(String text) {
		String url = "((https?|ftp|telnet|file|http):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
		Pattern pattern = Pattern.compile(url, Pattern.CASE_INSENSITIVE);
		Matcher match = pattern.matcher(text);
		int i = 0;
		while (match.find()) {
			text = text.replaceAll(match.group(i), "").trim();
			i++;
		}

		return removeHTMLChar(text);
	}

	/**
	 * Public tweets contains certain reserve character of HTML such as &amp,
	 * &quote This method cleans such HTMl reserve characters from the text
	 * using Regular Expression.
	 * 
	 * @param text
	 * @return
	 */

	private static String removeHTMLChar(String text) {

		return text.replaceAll("&amp;", "&").replaceAll("&quot;", "\"")
				.replaceAll("&apos;", "'").replaceAll("&lt;", "<")
				.replaceAll("&gt;", ">");
	}

	/**
	 * Entry point for this program
	 * 
	 * @param args
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws IOException, ClassNotFoundException {

	//	@SuppressWarnings({ "resource" })
	//	BeanFactory cntx = new GenericXmlApplicationContext("mongo-context.xml");
	//	MongoOperations operation = cntx.getBean("mongoTemplate",MongoOperations.class);// Connects to mongodb
/*
		FileWriter writer;
		try {
			writer = new FileWriter("output.txt");

			for (Object tweet : operation.getCollection("dataset").distinct("text")) { //"dataset" parameter is the name of the collection
				               // "text" parameter is text record inside the Json object that we want to extract
				try {
					writer.write(RunMe.removeUrl(tweet.toString()) + "\n");// Clean															// file
				} catch (Exception e) {
					continue;
				}
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
*/
	//	Model buildModel = (Model)cntx.getBean("training"); //dependency injection
		//buildModel.train(); // creating model to classify whether tweet is
							// positive or negative
		startTime=System.currentTimeMillis();
		RunMe.startMapReduce();
	}

	/**
	 * This method starts map reduce job
	 * @throws IOException 
	 */
	private static void startMapReduce() throws IOException, ClassNotFoundException {
	//	@SuppressWarnings({ "resource", "unused" })
	//	ApplicationContext cntx = new ApplicationContext("hadoop-context.xml"); // starting MapReduce job
		//ApplicationContext ctx = new ApplicationContext("hadoop-context.xml");
		 Configuration conf1 = new Configuration();
		 JobConf conf = new JobConf(conf1,RunMe.class);
		    conf.setJobName("sentimentanalysis");
		    conf.setJarByClass(RunMe.class);
		    conf.setOutputKeyClass(Text.class);
		    conf.setOutputValueClass(IntWritable.class);
		    conf.setMapOutputKeyClass(Text.class);
		    conf.setMapOutputValueClass(IntWritable.class);
		    conf.setMapperClass(Map.class);
		    conf.setCombinerClass(Reduce.class);
		    conf.setReducerClass(Reduce.class);
		    conf.setInputFormat(org.apache.hadoop.mapred.TextInputFormat.class);
		    conf.setOutputFormat(org.apache.hadoop.mapred.TextOutputFormat.class);
		    //"hdfs://localhost:54310/LMC/test10.csv"
		    FileInputFormat.setInputPaths(conf, new Path("/home/vikas/dst/t/LMC/big/20.csv"));
		    FileOutputFormat.setOutputPath(conf, new Path("/home/vikas/dst/t/LMC/big/LMCMR20"));
		   // file = new File("/home/vikas/Desktop/t");
		    
	        	
	        	
	   		// String st;
	    RunMe r= new RunMe();
			//System.out.println();
	    String	st = r.TrainData();
			 System.out.println("******"+st+"*****");  
	   		      	
	        
		    JobClient.runJob(conf);
		    System.out.println("after");
		    RunMe.endTime = System.currentTimeMillis();
			long totalTime = RunMe.endTime - RunMe.startTime;
			System.out.println("TN"+Map.TN);
			System.out.println("FN"+Map.FN);
			System.out.println("TP"+Map.TP);
			System.out.println("FP"+Map.FP);
			System.out.println("pos"+Map.p+"neg"+Map.n);
			
			
			System.out.println("Execution time is"+totalTime);
			
	}
	String TrainData() throws IOException, ClassNotFoundException {
	   	 System.out.println("inside train");
	   	File trainDir;
	   	String[] categories;
	   	LMClassifier class1;
	   	trainDir = new File("/home/vikas/dst/t/LMC/big/training/20/t");
	   	categories = trainDir.list();
	   	int nGram = 7;
	   	class1= DynamicLMClassifier.createNGramProcess(categories, nGram);

	   	for (int i = 0; i < categories.length; ++i) {
	   		String category = categories[i];
	   		Classification classification = new Classification(category);
	   		File file = new File(trainDir, categories[i]);
	   		File[] trainFiles = file.listFiles();
	   		for (int j = 0; j < trainFiles.length; ++j) {
	   			File trainFile = trainFiles[j];
	   			String review = Files.readFromFile(trainFile, "ISO-8859-1");
	   			Classified classified = new Classified(review, classification);
	   			   ((ObjectHandler) class1).handle(classified); 
	   			  
	   		}
	    	}
	   	AbstractExternalizable.compileTo((Compilable) class1, new File("/home/vikas/dst/t/LMC/big/training/20/classifier20mr.txt"));

	   //System.out.println("done");
	   return "Data trained";
	   }
	    
	    
}
