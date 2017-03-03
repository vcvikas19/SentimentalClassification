package com.vikas.tweetopennlpHadoop;


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


public class RunMe1 {
	/**
	 * Remove url from the string using regular expression
	 * 
	 * @param text
	 * @return URL free text
	 */
	 public static long startTime; 
		public static long endTime;
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
	 */
	public static void main(String[] args) throws IOException {

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
		RunMe1.startMapReduce();
	}

	/**
	 * This method starts map reduce job
	 * @throws IOException 
	 */
	private static void startMapReduce() throws IOException {
	//	@SuppressWarnings({ "resource", "unused" })
	//	ApplicationContext cntx = new ApplicationContext("hadoop-context.xml"); // starting MapReduce job
		//ApplicationContext ctx = new ApplicationContext("hadoop-context.xml");
		 Configuration conf1 = new Configuration();
		 JobConf conf = new JobConf(conf1, RunMe1.class);
		    conf.setJobName("sentimentanalysis");
		    conf.setJarByClass(RunMe1.class);
		    conf.setOutputKeyClass(Text.class);
		    conf.setOutputValueClass(IntWritable.class);
		    conf.setMapOutputKeyClass(Text.class);
		    conf.setMapOutputValueClass(IntWritable.class);
		    conf.setMapperClass(Map1.class);
		    conf.setCombinerClass(Reduce1.class);
		    conf.setReducerClass(Reduce1.class);
		    conf.setInputFormat(org.apache.hadoop.mapred.TextInputFormat.class);
		    conf.setOutputFormat(org.apache.hadoop.mapred.TextOutputFormat.class);
		    //"/home/vikas/dst/1/finaldataset/sendertwitter/ds/2"
		    //"hdfs://localhost:54310/1/60/test.csv"
		    FileInputFormat.setInputPaths(conf, new Path("/home/vikas/dst/t/NLP/big/50.csv"));
		    FileOutputFormat.setOutputPath(conf, new Path("/home/vikas/dst/t/NLP/big/50MR"));
		   // file = new File("/home/vikas/Desktop/t");
		   // JobClient.runJob(conf);
		    System.out.println("******before*****");
		    System.out.println(JobClient.runJob(conf));
		    System.out.println("after");
		    RunMe1.endTime = System.currentTimeMillis();
			long totalTime = RunMe1.endTime - RunMe1.startTime;
			System.out.println("TN"+Map1.TN);
			System.out.println("FN"+Map1.FN);
			System.out.println("TP"+Map1.TP);
			System.out.println("FP"+Map1.FP);
			System.out.println("pos"+Map1.p+"neg"+Map1.n);
			
			System.out.println("Execution train time is"+totalTime);
	}

}
