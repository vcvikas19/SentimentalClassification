package com.vikas.tweetopennlp;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import com.vikas.LMC.TwitterManager;

import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DocumentSampleStream;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;




public class OpenNLPCategorizer {
    	DoccatModel model;
	
	static long p=0;
	static long n =0;
     static int TP=0;
     static int FN=0;
     static int TN=0;
     static int FP=0;
   	 public static long startTime; 
   		public static long endTime; 
	public  static void main(String[] args) {
		startTime = System.currentTimeMillis();
		OpenNLPCategorizer twitterCategorizer = new OpenNLPCategorizer();
		twitterCategorizer.trainModel();
		
		 BufferedReader br = null;

			try {

				String sCurrentLine;
//SentimentClassifier sm = new SentimentClassifier();
				br = new BufferedReader(new FileReader("/home/vikas/dst/t/NLP/sender/50.csv"));
				
	            //for the first line it'll print
	            
				while ((sCurrentLine = br.readLine()) != null) 
				{
				twitterCategorizer.classifyNewTweet(sCurrentLine);
					
				}

		 
			}
			 catch (IOException e) {
					e.printStackTrace();
				}  
			endTime   = System.currentTimeMillis();
			long totalTime = endTime - startTime;
			System.out.println(p+"neg="+n);
			System.out.println("TN"+TN);
			System.out.println("FN"+FN);
			System.out.println("TP"+TP);
			System.out.println("FP"+FP);
			System.out.println(totalTime+"****time");
		 
	}
		
	public void trainModel() {
	
		InputStream dataIn = null;
		try {
			dataIn = new FileInputStream("/home/vikas/dst/t/NLP/sender/50tr.txt");
			ObjectStream lineStream = new PlainTextByLineStream(dataIn, "UTF-8");
			ObjectStream sampleStream = new DocumentSampleStream(lineStream);
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
	}


	public String classifyNewTweet(String tweet) {
		String[] arr = tweet.split(",");
		DocumentCategorizerME myCategorizer = new DocumentCategorizerME(model);
		double[] outcomes = myCategorizer.categorize(arr[1]);
		String category = myCategorizer.getBestCategory(outcomes);
  
		
		System.out.println("*"+tweet);
		System.out.println("arr[0] = " + arr[0]); // h
        System.out.println("arr[1] = " + arr[1]); // Vito
		
		
		if (category.equalsIgnoreCase("1")) {
			if(arr[0].equals("1"))
			{
				TP++;
			}
			if(arr[0].equals("0"))
			{
				FP++;
			}
			p++;
			System.out.println("The tweet is positive :) ");
			return "";
		} else {
			if(arr[0].equals("0"))
			{
				TN++;
			}
			if(arr[0].equals("1"))
			{
				FN++;
			}
			n++;
			System.out.println("The tweet is negative :( ");
			return "";
		}
		
		
		
	}
}