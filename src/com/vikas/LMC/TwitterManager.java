package com.vikas.LMC;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JFrame;



import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterManager {
 
	SentimentClassifier sentClassifier;
	int LIMIT= 500; 
	ConfigurationBuilder cb;
	Twitter twitter;
	  public static long startTime; 
		public static long endTime;  
		static int TP=0;
	     static int FN=0;
	     static int TN=0;
	     static int FP=0;
	public String performQuery(String tweet) throws InterruptedException, IOException { 
		 
		
	                String sent = sentClassifier.classify(tweet);  
	               System.out.println("Sentiment: " + sent);   
	               
	                return sent;
	         
	    }  
	public void analyze() throws IOException, TwitterException{
		//Create frame for GUI
		GUIFrame gui = new GUIFrame(); // create EventsFrame
		gui.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		gui.setSize( 750, 500 ); // set frame size
		gui.setVisible( true ); // display frame
	}
	 public static void main(String []args) throws InterruptedException, IOException, TwitterException, ClassNotFoundException
	    {
		  startTime = System.currentTimeMillis();
		 //TwitterManager twitterManager = new TwitterManager();  
		// twitterManager.analyze();
		  
		 train t = new train();
		 String st = t.TrainData();
		 System.out.println("******"+st+"*****");
		  
		
		  
		 BufferedReader br = null;

			try {

				String sCurrentLine;
SentimentClassifier sm = new SentimentClassifier();
    int p=0;
    int n=0;
    
				br = new BufferedReader(new FileReader("/home/vikas/dst/t/LMC/sender/50.txt"));

				while ((sCurrentLine = br.readLine()) != null) 
				{
					String[] arr = sCurrentLine.split("\t");
					String s=sm.classify(arr[1]);
				
					System.out.println(arr[1]);
					System.out.println(s);
					if(s.equals("pos"))
					{p++;
					if(arr[0].equals("1"))
					{
						TP++;
					}
					if(arr[0].equals("0"))
					{
						FP++;
					}
					
					
					
					}else
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
					
				}
				
   
 System.out.println("***pos"+p+"neg"+n);
 TwitterManager.endTime   = System.currentTimeMillis();
	long totalTime = TwitterManager.endTime - TwitterManager.startTime;
	//double ac = accuracy(pc,nc);
	System.out.println("TN"+TN);
	System.out.println("FN"+FN);
	System.out.println("TP"+TP);
	System.out.println("FP"+FP);
	
	
	System.out.println("Execution time is"+totalTime);
		 
			}
			 catch (IOException e) {
					e.printStackTrace();
				} 
		 
		 
		
	//	twitterManager.performQuery("SrBachchan"); 
		// twitterManager.performQuery("vcvikas19");
			}
	    
	
	
	
	
	 }  