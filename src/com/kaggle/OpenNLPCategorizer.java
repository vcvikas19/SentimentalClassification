package com.kaggle;
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
				br = new BufferedReader(new FileReader("/home/vikas/dst/1/kagglespecial/kaggle/testdata.txt"));

				while ((sCurrentLine = br.readLine()) != null) 
				{
					System.out.println("*"+sCurrentLine);
					twitterCategorizer.classifyNewTweet(sCurrentLine);
				
					
				}

		 
			}
			 catch (IOException e) {
					e.printStackTrace();
				}  
			endTime   = System.currentTimeMillis();
			long totalTime = endTime - startTime;
			System.out.println(p+"neg="+n);
			System.out.println(totalTime+"****time");
		 
	}
		
	public void trainModel() {
	
		InputStream dataIn = null;
		try {
			dataIn = new FileInputStream("/home/vikas/dst/1/kagglespecial/kaggle/training.txt");
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
		
		DocumentCategorizerME myCategorizer = new DocumentCategorizerME(model);
		double[] outcomes = myCategorizer.categorize(tweet);
		String category = myCategorizer.getBestCategory(outcomes);

		if (category.equalsIgnoreCase("1")) {
			p++;
			System.out.println("The tweet is positive :) ");
			return "";
		} else {
			n++;
			System.out.println("The tweet is negative :( ");
			return "";
		}
		
		
		
	}
}