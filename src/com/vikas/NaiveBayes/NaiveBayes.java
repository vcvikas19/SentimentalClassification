package com.vikas.NaiveBayes;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
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
import org.apache.mahout.vectorizer.SparseVectorsFromSequenceFiles;
import org.apache.mahout.vectorizer.TFIDF;

import com.google.common.collect.ConcurrentHashMultiset;
import com.google.common.collect.Multiset;

import twitter4j.TwitterException;
public class NaiveBayes 
{

	
    Configuration configuration = new Configuration();
  /*  String        inputFilePath = "/home/vikas/Desktop/inputd3/test.csv";
    String        sequenceFilePath = "/home/vikas/Desktop/inputd3/tweets-seq";
    String        labelIndexPath = "/home/vikas/Desktop/inputd3/labelindex";
    String        modelPath = "/home/vikas/Desktop/inputd3/model";
    String        vectorsPath = "/home/vikas/Desktop/inputd3/tweets-vectors";
    String        dictionaryPath = "/home/vikas/Desktop/inputd3/tweets-vectors/dictionary.file-0";
    String        documentFrequencyPath = "/home/vikas/Desktop//inputd3/tweets-vectors/df-count/part-r-00000"; 
  */
    //
    String        inputFilePath = "/home/vikas/dst/1/finaldataset/1/60/NB/DT2/input/train10.csv";
    String        sequenceFilePath = "/home/vikas/dst/1/finaldataset/1/60/NB/DT2/input/tweets-seq";
    String        labelIndexPath = "/home/vikas/dst/1/finaldataset/1/60/NB/DT2/input/labelindex";
    String        modelPath = "/home/vikas/dst/1/finaldataset/1/60/NB/DT2/input/model";
    String        vectorsPath = "/home/vikas/dst/1/finaldataset/1/60/NB/DT2/input/tweets-vectors";
    String        dictionaryPath = "/home/vikas/dst/1/finaldataset/1/60/NB/DT2/input/tweets-vectors/dictionary.file-0";
    String        documentFrequencyPath = "/home/vikas/dst/1/finaldataset/1/60/NB/DT2/input/tweets-vectors/df-count/part-r-00000"; 
   
    
    
    
    public static long startTime; 
	public static long endTime;  
	public static int pc=0;
	public static int nc=0;
public void analyze() throws IOException, TwitterException{
	//Create frame for GUI
	GUIFrame gui = new GUIFrame(); // create EventsFrame
	gui.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
	gui.setSize( 750, 500 ); // set frame size
	gui.setVisible( true ); // display frame
}//end method
public static void main(String[] args) throws Throwable {
     startTime = System.currentTimeMillis();
	
NaiveBayes nb = new NaiveBayes();
nb.inputDataToSequenceFile();
nb.sequenceFileToSparseVector();
//System.out.println("hhhh*********");
nb.trainNaiveBayesModel();
System.out.println("***************hhhh*********");
//nb.analyze();


//nb.inputDataToSequenceFile();

//nb.sequenceFileToSparseVector();
//nb.trainNaiveBayesModel();
//for(int i=0;i<5;i++)
//{
//	Scanner sc = new Scanner(System.in);
//	String line = sc.nextLine();
//
//	nb.classifyNewTweet(line);
//}
String fileName = "/home/vikas/dst/1/finaldataset/1/60/NB/DT2/input/test10.csv";
long k =0;
try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {

	String line;
	while ((line = br.readLine()) != null) {
		
		System.out.println(line);
		nb.classifyNewTweet(line);
	}

} catch (IOException e) {
	e.printStackTrace();
}


/*
BufferedReader br = null;

try {

	String sCurrentLine;
//SentimentClassifier sm = new SentimentClassifier();
	br = new BufferedReader(new FileReader("/home/vikas/Desktop/inputd3/testgold.csv"));

	while ((sCurrentLine = br.readLine()) != null) 
	{
		System.out.println("*"+sCurrentLine);
	nb.classifyNewTweet(sCurrentLine);
	
		
	}


}
 catch (IOException e) {
		e.printStackTrace();
	}  
	*/
endTime = System.currentTimeMillis();
  long etime = endTime-startTime;
  System.out.println(pc+"neg"+nc);
  System.out.println("etime"+etime);
}

public void inputDataToSequenceFile() throws Exception {

BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));

FileSystem fs = FileSystem.getLocal(configuration);

Path seqFilePath = new Path(sequenceFilePath);

fs.delete(seqFilePath, false);

SequenceFile.Writer writer = SequenceFile.createWriter(fs,configuration, seqFilePath, Text.class, Text.class);

int count = 0;

try {

String line;

while ((line = reader.readLine()) != null) {

String[] tokens = line.split(",");
//System.out.println("success");
writer.append(new Text("/" + tokens[0] + "/tweet" + count++),new Text(tokens[1]));

}

} finally {

reader.close();

writer.close();

}

}

void sequenceFileToSparseVector() throws Exception {

SparseVectorsFromSequenceFiles svfsf = new SparseVectorsFromSequenceFiles();

svfsf.run(new String[] { "-i", sequenceFilePath, "-o", vectorsPath,"-ow" });

}

void trainNaiveBayesModel() throws Exception {

TrainNaiveBayesJob trainNaiveBayes = new TrainNaiveBayesJob();

trainNaiveBayes.setConf(configuration);

trainNaiveBayes.run(new String[] { "-i",vectorsPath + "/tfidf-vectors", "-o", modelPath, "-li",labelIndexPath, "--input", "-c", "-ow" });

}

public String classifyNewTweet(String tweet) throws IOException {

//System.out.println("Tweet: " + tweet);

Map<String, Integer> dictionary = readDictionary(configuration,new Path(dictionaryPath));

Map<Integer, Long> documentFrequency = readDocumentFrequency(configuration, new Path(documentFrequencyPath));

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
NaiveBayesModel model = NaiveBayesModel.materialize(new Path(modelPath), configuration);
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
public static Map<String, Integer> readDictionary(Configuration conf,Path dictionnaryPath) {

Map<String, Integer> dictionnary = new HashMap<String, Integer>();

for (Pair<Text, IntWritable> pair : new SequenceFileIterable<Text, IntWritable>(dictionnaryPath, true, conf)) 
{

dictionnary.put(pair.getFirst().toString(), pair.getSecond().get());

}

return dictionnary;

}

public static Map<Integer, Long> readDocumentFrequency(Configuration conf,Path documentFrequencyPath) {

Map<Integer, Long> documentFrequency = new HashMap<Integer, Long>();

for (Pair<IntWritable, LongWritable> pair : new SequenceFileIterable<IntWritable, LongWritable>(documentFrequencyPath, true, conf)) 
{

documentFrequency.put(pair.getFirst().get(), pair.getSecond().get());

}

return documentFrequency;

}

}