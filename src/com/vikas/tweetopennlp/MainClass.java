package com.vikas.tweetopennlp;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.swing.JFrame;

import com.vikas.LMC.SentimentClassifier;
import com.vikas.LMC.TwitterManager;

import opennlp.tools.doccat.DoccatModel;
import twitter4j.TwitterException;

public class MainClass {

	 public static long startTime; 
		public static long endTime; 
	public static void main(String[] args) throws TwitterException, IOException{  
		 startTime = System.currentTimeMillis();
	//	MainClass algo = new MainClass();
		//algo.analyze();
		//algo.populateTextFile();
		 
		 BufferedReader br = null;

			try {

				String sCurrentLine;
//SentimentClassifier sm = new SentimentClassifier();
				OpenNLPCategorizer op = new OpenNLPCategorizer();
 int p=0;
 int n=0;
 
				br = new BufferedReader(new FileReader("/home/vikas/Desktop/aaa/positive.txt"));

				while ((sCurrentLine = br.readLine()) != null) 
				{
					System.out.println("*"+sCurrentLine);
				op.classifyNewTweet(sCurrentLine);
				
					
				}
System.out.println("***pos"+p+"neg"+n);
TwitterManager.endTime   = System.currentTimeMillis();
	long totalTime = TwitterManager.endTime - TwitterManager.startTime;
	//double ac = accuracy(pc,nc);
	System.out.println("Execution time is"+totalTime);
		 
			}
			 catch (IOException e) {
					e.printStackTrace();
				}  
		 
		 
		 
		 
	}//end main
	
	public void analyze() throws IOException, TwitterException{
		//Create frame for GUI
		GUIFrame gui = new GUIFrame(); // create EventsFrame
		gui.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		gui.setSize( 750, 500 ); // set frame size
		gui.setVisible( true ); // display frame
	}//end method
	
	public void populateTextFile() throws TwitterException, IOException{
		String happyFace = ":)";
		String sadFace = ":(";
		String happyFace2 = ":-)";
		String sadFace2 = ":-(";
		TwitterData twitter = new TwitterData();
		String searchTerm = ":(&lang:en";
		ArrayList<String> tweets = twitter.search(searchTerm);
		for(int i=0; i<tweets.size(); i++){
			StringTokenizer text = new StringTokenizer(tweets.get(i));
			String newString = "";
			while(text.hasMoreTokens()){
				String tempString = text.nextToken();  
				if(!tempString.contains("#") && !tempString.contains("@") && !tempString.contains("http://t.co") && !tempString.contains("https://t.co") && !tempString.contains(happyFace) && !tempString.contains(sadFace) && !tempString.contains(":p") && !tempString.contains(":P") && !tempString.contains(":D") && !tempString.contains(sadFace2) && !tempString.contains(happyFace2) && !tempString.contains("RT") && !tempString.contains("♥") && !tempString.contains("<3") && !tempString.contains("rt")){
					newString = newString+" "+tempString;
				}//end if
			}//end while
			writeToFile(newString);
			System.out.println(newString);
			//System.out.println(" ");
		}//end for
	}//end method
	
	public void writeToFile(String text) throws IOException{
		//Write to txt file
		String dataFile = "/home/vikas/Desktop/textfile";

		//Writing to file
		try {
		    BufferedWriter out = new BufferedWriter(new FileWriter(dataFile,true));
		    out.newLine();
		    out.write(text);
		    out.close();
		} catch (IOException e) {}
	}//end method
}//end class
