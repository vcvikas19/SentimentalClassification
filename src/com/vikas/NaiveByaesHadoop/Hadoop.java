package com.vikas.NaiveByaesHadoop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;
import org.apache.mahout.classifier.naivebayes.NaiveBayesModel;
import org.apache.mahout.classifier.naivebayes.StandardNaiveBayesClassifier;
import org.apache.mahout.classifier.naivebayes.training.TrainNaiveBayesJob;
import org.apache.mahout.common.Pair;
import org.apache.mahout.common.iterator.sequencefile.SequenceFileIterable;
import org.apache.mahout.math.RandomAccessSparseVector;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.Vector.Element;
import org.apache.mahout.vectorizer.TFIDF;

import com.google.common.collect.ConcurrentHashMultiset;
import com.google.common.collect.Multiset;

import twitter4j.TwitterException;

public class Hadoop {
	Configuration        conf = new Configuration();

	static String        inputFilePath = "hdfs://localhost:54310/user/vikas/test/twit.txt";
	static String        sequenceFilePath = "hdfs://localhost:54310/input(i)/tweets-seq";
	static String        labelIndexPath = "hdfs://localhost:54310/inputn/labelindex";
	static String        modelPath = "hdfs://localhost:54310/inputn/input/model";
	static String        vectorsPath = "hdfs://localhost:54310/inputn/tweets-vectors";
	static String        dictionaryPath = "hdfs://localhost:54310/inputn/tweets-vectors/dictionary.file-0";
	static String        documentFrequencyPath = "hdfs://localhost:54310/inputn/tweets-vectors/df-count/part-r-00000"; 
	static String        readfile ="hdfs://localhost:54310/user/dataset.csv";
	///test111/input/tweets-vectors
	///user/vikas/test111/input/tweets-vectors
	 public static long startTime; 
		public static long endTime;  
	public static int pc=0;
	public static int nc=0;
	
	public void analyze() throws IOException, TwitterException{
		//Create frame for GUI
		GUIFrameforHadoop gui = new GUIFrameforHadoop(); // create EventsFrame
		gui.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		gui.setSize( 750, 500 ); // set frame size
		gui.setVisible( true ); // display frame
	}//end method
	public static void main(String[] args) throws Throwable {
		 startTime = System.currentTimeMillis();
		Hadoop nb = new Hadoop();
/*	nb.delete(sequenceFilePath);
		nb.delete( labelIndexPath);
		nb.delete(modelPath);
		nb.delete(vectorsPath);
		nb.delete(dictionaryPath);
		nb.delete(documentFrequencyPath); */
	//nb.mkdir("hdfs://localhost:54310/inputn");
	//nb.mkdir("hdfs://localhost:54310/inputn/tweets-vectors");
//	nb.mkdir("hdfs://localhost:54310/inputn/tweets-vectors/df-count");
	//nb.mkdir(sequenceFilePath);
//	nb.mkdir(labelIndexPath);
	
//	nb.mkdir(modelPath);
//	nb.mkdir(vectorsPath);
//	nb.mkdir(dictionaryPath);
	//nb.mkdir(documentFrequencyPath);
	//nb.mkdir(readfile);
	
	//nb.inputDataToSequenceFile();
   // nb.sequenceFileToSparseVector();
   // nb.trainNaiveBayesModel();
	//nb.checkFile(readfile);
		
		//nb.classifyNewTweet("i am happy");
	nb.analyze();
    System.out.println("all done");
		}

		public void inputDataToSequenceFile() throws Exception {
			
			Configuration conf = new Configuration();
	        conf.addResource(new Path("/usr/local/hadoop/etc/hadoop/core-site.xml"));
	        conf.addResource(new Path("/usr/local/hadoop/etc/hadoop/hdfs-site.xml"));
	        FileSystem fs= FileSystem.get(conf);

	        Path path = new Path(inputFilePath);
	        
	        FSDataInputStream in = fs.open(path);
	        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
	        //  String line = reader.readLine();
	          Path seqFilePath = new Path(sequenceFilePath);
	         fs.delete(seqFilePath, false);
	         @SuppressWarnings("deprecation")
	          SequenceFile.Writer writer = SequenceFile.createWriter(fs,conf, seqFilePath, Text.class, Text.class);

	  int count = 0;

	  try {
	  String line;
	  while ((line = reader.readLine()) != null) {
  	  String[] tokens = line.split("\t");
	  writer.append(new Text("/" + tokens[0] + "/tweet" + count++),
	  new Text(tokens[1]));
	  }
	  } finally {

	  reader.close();

	  writer.close();

	  }
		}

		void sequenceFileToSparseVector() throws Exception {
			//Configuration conf = new Configuration();
	       	SparseVectorsFromSequenceFiles1 svfsf = new SparseVectorsFromSequenceFiles1();
     		svfsf.run(new String[] { "-i",sequenceFilePath, "-o", vectorsPath,"-ow" });

		}

		void trainNaiveBayesModel() throws Exception {

		TrainNaiveBayesJob trainNaiveBayes = new TrainNaiveBayesJob();
		
        conf.addResource(new Path("/usr/local/hadoop/etc/hadoop/core-site.xml"));
        conf.addResource(new Path("/usr/local/hadoop/etc/hadoop/hdfs-site.xml"));

		trainNaiveBayes.setConf(conf);

		trainNaiveBayes.run(new String[] { "-i",vectorsPath + "/tfidf-vectors", "-o", modelPath, "-li",labelIndexPath, "--input", "-c", "-ow" });

		}

		public String classifyNewTweet(String tweet) throws IOException {

			//System.out.println("Tweet: " + tweet);

			Map<String, Integer> dictionary = readDictionary(conf,new Path(dictionaryPath));

			Map<Integer, Long> documentFrequency = readDocumentFrequency(conf, new Path(documentFrequencyPath));

			Multiset<String> words = ConcurrentHashMultiset.create();

			// Extract the words from the new tweet using Lucene

			Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_46);

			TokenStream tokenStream = analyzer.tokenStream("text",new StringReader(tweet));

			CharTermAttribute termAttribute = tokenStream.addAttribute(CharTermAttribute.class);

			tokenStream.reset();

			int wordCount = 0;

			while (tokenStream.incrementToken()) {

			if (termAttribute.length() > 0) {

			String word = tokenStream.getAttribute(CharTermAttribute.class).toString();

			Integer wordId = dictionary.get(word);

			// If the word is not in the dictionary, skip it

			if (wordId != null) {

			words.add(word);

			wordCount++;

			}

			}

			}

			tokenStream.end();

			tokenStream.close();

			int documentCount = documentFrequency.get(-1).intValue();

			// Create a vector for the new tweet (wordId => TFIDF weight)

			Vector vector = new RandomAccessSparseVector(10000);

			TFIDF tfidf = new TFIDF();

			for (Multiset.Entry<String> entry : words.entrySet()) {

			String word = entry.getElement();

			int count = entry.getCount();

			Integer wordId = dictionary.get(word);

			Long freq = documentFrequency.get(wordId);

			double tfIdfValue = tfidf.calculate(count, freq.intValue(),wordCount, documentCount);

			vector.setQuick(wordId, tfIdfValue);

			}

			// Model is a matrix (wordId, labelId) => probability score
			NaiveBayesModel model = NaiveBayesModel.materialize(new Path(modelPath), conf);
			StandardNaiveBayesClassifier classifier = new StandardNaiveBayesClassifier(model);

			// With the classifier, we get one score for each label.The label with

			// the highest score is the one the tweet is more likely to be

			// associated to

			Vector resultVector = classifier.classifyFull(vector);

			double bestScore = -Double.MAX_VALUE;

			int bestCategoryId = -1;

			for (Element element : resultVector.all()) {

			int categoryId = element.index();

			double score = element.get();
			if (score > bestScore) {

			bestScore = score;

			bestCategoryId = categoryId;

			}

			if (categoryId == 1) {

			System.out.println("Score of being positive: " + score);

			} else {

			System.out.println("Score of being negative: " + score);

			}

			}
			analyzer.close();
			if (bestCategoryId == 1) {
              pc++;
			System.out.println("The tweet is positive :) ");
			return "positive";

			} else {
             nc++;
			System.out.println("The tweet is negative :( ");
			return "negative";

			}
          


			}
		public static double accuracy(int p,int n)
	      {
			double total= (double)p+(double)n;
				double pp= (p/total)*100;
				double np= (n/total)*100;
				System.out.println("positive tweets are"+p);
				System.out.println("negative tweets are"+n);
				System.out.println("total tweets are"+total);
				System.out.println("positive percentage is"+pp);
				System.out.println("negative percentage count is"+np);
	      	
	      	return 1.0;
	      	
	      }
		public static Map<String, Integer> readDictionary(Configuration conf,

		Path dictionnaryPath) {

		Map<String, Integer> dictionnary = new HashMap<String, Integer>();

		for (Pair<Text, IntWritable> pair : new SequenceFileIterable<Text, IntWritable>(

		dictionnaryPath, true, conf)) {

		dictionnary.put(pair.getFirst().toString(), pair.getSecond().get());

		}

		return dictionnary;

		}

		public static Map<Integer, Long> readDocumentFrequency(Configuration conf,

		Path documentFrequencyPath) {

		Map<Integer, Long> documentFrequency = new HashMap<Integer, Long>();

		for (Pair<IntWritable, LongWritable> pair : new SequenceFileIterable<IntWritable, LongWritable>(

		documentFrequencyPath, true, conf)) {

		documentFrequency

		.put(pair.getFirst().get(), pair.getSecond().get());

		}

		return documentFrequency;

		}	
	public void mkdir(String dir2) throws IOException {
		   
		   String dir =dir2;
	        Configuration conf = new Configuration();
	        conf.addResource(new Path("/usr/local/hadoop/etc/hadoop/core-site.xml"));

	        FileSystem fileSystem = FileSystem.get(conf);

	        Path path = new Path(dir);
	      
	        fileSystem.mkdirs(path);
	
	       
	        fileSystem.close();
	    }
	
	   public void delete(String file) throws IOException {
	        Configuration conf = new Configuration();
	        conf.addResource(new Path("/usr/local/hadoop/etc/hadoop/core-site.xml"));
	        conf.addResource(new Path("/usr/local/hadoop/etc/hadoop/hdfs-site.xml"));
			

	        FileSystem fileSystem = FileSystem.get(conf);

	        Path path = new Path(file);
	        if (!fileSystem.exists(path)) {
	            //.println("File " + file + " does not exists");
	            return;
	        }

	        fileSystem.delete(new Path(file), true);
	        
	        fileSystem.close();

	   }
	   
		
		@SuppressWarnings("deprecation")
		public int aFile(InputStream inp, String b, String des, String id) throws IOException {
			String[] a=new String[6];
			a[0]=id;
			a[1]=b;
			a[2]=new java.util.Date().toString();
			a[3]=des;
			int i=0;
			int z=1;
			String dest= "/"+des;
		
			InputStream in = inp;
			
			Configuration conf = new Configuration();
			conf.addResource(new Path("/usr/local/hadoop/etc/hadoop/core-site.xml"));
	        conf.addResource(new Path("/usr/local/hadoop/etc/hadoop/hdfs-site.xml"));
			FileSystem fileSystem = FileSystem.get(conf);
            String filename = b;
	        // Create the destination path including the filename.
		    if (dest.charAt(dest.length() - 1) != '/')
		    {
		            dest = dest + "/" + filename;
		        } else {
		            dest = dest + filename;
		        }

		        Path path = new Path(dest);
		        if (fileSystem.exists(path)) {
		        	return(z);
		        }

		        else{
		        // Create a new file and write data to it.
		        FSDataOutputStream out = fileSystem.create(path);
		        IOUtils.copyBytes(in, out, 4096, true);
		        z=11;
		        Path path2 = new Path("/DON/log.txt");
		        
		        if (fileSystem.exists(path2)) {
		            FSDataOutputStream out2 = fileSystem.append(path2);
			       	 for(i=0;i<4;i++)
			       	 {if(a[i] == "")
			       	 {
			       		 a[i]= "EMPTY";
			       		 }
			       	 out2.writeBytes(a[i]+"\t");
			       	 }
			       	 out2.writeBytes("\n");
			       	 out2.flush();
			       	 out2.sync();
			       	 
			       	 
			            fileSystem.close();
		        	return(z);
		        }
		        
		        else{
		        	FSDataOutputStream out2 = fileSystem.create(path2);
		       	 for(i=0;i<4;i++)
		       	 {if(a[i] == "")
		       	 {
		       		 a[i]= "EMPTY";
		       		 }
		       	 out2.writeBytes(a[i]+"\t");
		       	 }
		       	 out2.writeBytes("\n");
		       	 out2.flush();
		       	 out2.sync();
		       	 
		       	 
		            fileSystem.close();
	        	return(z);
		        }
		        
		       
		        }
		}	   
	   
		
		 @SuppressWarnings("deprecation")
		public void write (String[] a) throws IOException {
			 int i;
			 
				
			   String dir = "/DON/information.txt";
		        Configuration conf = new Configuration();
		        conf.addResource(new Path("/usr/local/hadoop/etc/hadoop/core-site.xml"));
		        conf.addResource(new Path("/usr/local/hadoop/etc/hadoop/hdfs-site.xml"));

		        FileSystem fs= FileSystem.get(conf);

		        Path path = new Path(dir);
		        
		        if (fs.exists(path)){
		        	
		        	FSDataOutputStream out = fs.append(path);
			       	 for(i=0;i<6;i++)
			       	 {if(a[i] == "")
			       	 {
			       		 a[i]= "EMPTY";
			       		 }
			       	 out.writeBytes(a[i]+"\t");
			       	 }
			       	 out.writeBytes("\n");
			       	 out.flush();
			       	 out.sync();
			            fs.close();
		        	
		        }
		        else{
		        	FSDataOutputStream out = fs.create(path);
		        	 for(i=0;i<6;i++)
			       	 {if(a[i] == "")
			       	 {
			       		 a[i]= "EMPTY";
			       		 }
			       	 out.writeBytes(a[i]+"\t");
			       	 }
			       	 out.writeBytes("\n");
			       	 out.flush();
			       	 out.sync();
			            fs.close();
		        }
		       
		   }
		
		 
		 public int dd (String a, String b) throws IOException {
			 
			 int z=0000;
			 String dir = "/DON/information.txt";
			 Configuration conf = new Configuration();
		        conf.addResource(new Path("/usr/local/hadoop/etc/hadoop/core-site.xml"));
		        conf.addResource(new Path("/usr/local/hadoop/etc/hadoop/hdfs-site.xml"));

		        FileSystem fs= FileSystem.get(conf);

		        Path path = new Path(dir);
		     
		        if (fs.exists(path)){
		        	
		        }
		        else{
		        	fs.create(path);
		        }
		        
		        FSDataInputStream in = fs.open(path);
		        
		        
		        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		        
		       /* String line = reader.readLine();
		        String[] c=line.split("	");
		        .println(c[4]);
		        */
		     
	        	String line = reader.readLine();
	        	
	        	while ( line != null )
	        	{	
		        String[] c=line.split("	");
		        
		        if(c[0].equals(a) && c[1].equals(b) && c[0] != "" && c[1] !="" && !c[0].equals("EMPTY") && !c[1].equals("EMPTY"))
		        {   z=1100;
		        fs.close();
		        	break;
		        }
		        
		        
		       line= reader.readLine();
		        }fs.close();
	        	return z;
		        }
		
public int idcheck (String a) throws IOException {
			 
	 int z=11;
	 String dir = "/DON/information.txt";
	 Configuration conf = new Configuration();
     conf.addResource(new Path("/usr/local/hadoop/etc/hadoop/core-site.xml"));
     conf.addResource(new Path("/usr/local/hadoop/etc/hadoop/hdfs-site.xml"));

     FileSystem fs= FileSystem.get(conf);

     Path path = new Path(dir);
     if (fs.exists(path)){
     	
     }
     else{
     	fs.create(path);
     }
  
     FSDataInputStream in = fs.open(path);
     
     
     BufferedReader reader = new BufferedReader(new InputStreamReader(in));
     
     String line = reader.readLine();
 	
 	while ( line != null )
 	{	
     String[] c=line.split("	");
     
     if(a.equals(c[0])  || a == "")
     {   z=00;
     	break;
     }
     line= reader.readLine();
     }
 	fs.close();
 	return z;
     }


		 
	public int passcheck (String a, String b) throws IOException {
	 
		int y=0;
	
		if(a.equals(b) && a != "" && b !="" && !a.equals("EMPTY") && !b.equals("EMPTY"))
     {   
			y=11;
     	
     }
     else if(a == "" && b == "" || a.equals("EMPTY") && b.equals("EMPTY"))
     {
     	y=2;
     }
    
		return y;
	
    }

	public String user (String a, String b) throws IOException {
		 
		 String z= "unknown";
		 String dir = "/DON/information.txt";
		 Configuration conf = new Configuration();
	        conf.addResource(new Path("/usr/local/hadoop/etc/hadoop/core-site.xml"));
	        conf.addResource(new Path("/usr/local/hadoop/etc/hadoop/hdfs-site.xml"));

	        FileSystem fs= FileSystem.get(conf);

	        Path path = new Path(dir);
	     
	        FSDataInputStream in = fs.open(path);
	        
	        
	        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
	        
	       /* String line = reader.readLine();
	        String[] c=line.split("	");
	        .println(c[4]);
	        */
	     
    	String line = reader.readLine();
    	
    	while ( line != null )
    	{	
	        String[] c=line.split("	");
	        
	        if(c[0].equals(a) && c[1].equals(b) && c[0] != "" && c[1] !="" && !c[0].equals("EMPTY") && !c[1].equals("EMPTY"))
	        {   z= c[3]+"////"+c[0];
	        	break;
	        }
	        
	        
	       line= reader.readLine();
	        }
    	fs.close();
    	return z;
	        }
	
	@SuppressWarnings("deprecation")
	public void writetw (String[] a) throws IOException {
		 int i;
		 																	
			
		   String dir = "/DON/tweet.txt";
	        Configuration conf = new Configuration();
	        conf.addResource(new Path("/usr/local/hadoop/etc/hadoop/core-site.xml"));
	        conf.addResource(new Path("/usr/local/hadoop/etc/hadoop/hdfs-site.xml"));

	        FileSystem fs= FileSystem.get(conf);

	        Path path = new Path(dir);
	        
	        if (fs.exists(path)){
	        	
	        	FSDataOutputStream out = fs.append(path);
		       	
	        	for(i=0;i<4;i++)
	        	{
		       	 out.writeBytes(a[i]+"\t");
	        	}
		       	 out.writeBytes("\n");
		       	 out.flush();
		       	 out.sync();
		            fs.close();
	        }
	        else{
FSDataOutputStream out = fs.create(path);
		       	
	        	for(i=0;i<4;i++)
	        	{
		       	 out.writeBytes(a[i]+"\t");
	        	}
		       	 out.writeBytes("\n");
		       	 out.flush();
		       	 out.sync();
		            fs.close();
	        	
	        }
	       
	        
	        
	   }
   public void checkFile(String dir) throws IOException
   {
	   Configuration conf = new Configuration();
       conf.addResource(new Path("/usr/local/hadoop/etc/hadoop/core-site.xml"));
       conf.addResource(new Path("/usr/local/hadoop/etc/hadoop/hdfs-site.xml"));

       FileSystem fs= FileSystem.get(conf);

       Path path = new Path(dir);
    
       FSDataInputStream in = fs.open(path);
       
       
       BufferedReader reader = new BufferedReader(new InputStreamReader(in));
    
  	String line = reader.readLine();
  	Hadoop nb =new Hadoop();
  	while ( line != null )
  	{	nb.classifyNewTweet(line);
        line= reader.readLine();
        //Thread.sleep(2);
       }
  	fs.close();
	   
	  
   }
}
