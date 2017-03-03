package com.vikas.LMC;

import java.io.File;
import java.io.IOException;

import com.aliasi.classify.Classification;
import com.aliasi.classify.Classified;
import com.aliasi.classify.DynamicLMClassifier;
import com.aliasi.classify.LMClassifier;
import com.aliasi.corpus.ObjectHandler;
import com.aliasi.util.AbstractExternalizable;
import com.aliasi.util.Compilable;
import com.aliasi.util.Files;

public class train
{
  /*public static void main(String [] args)
  {
	  train t = new train();
	
  }
  */
 String TrainData() throws IOException, ClassNotFoundException {
	 System.out.println("inside train");
	File trainDir;
	String[] categories;
	LMClassifier class1;
	trainDir = new File("/home/vikas/dst/t/LMC/sender/t50/t");
	categories = trainDir.list();
	System.out.println(categories.length);
	int nGram = 7;
	class1= DynamicLMClassifier.createNGramProcess(categories, nGram);
	
	for (int i = 0; i < categories.length; ++i) {
		String category = categories[i];
		
		Classification classification = new Classification(category);
		File file = new File(trainDir, categories[i]);
		System.out.println(file.listFiles());
		File[] trainFiles = file.listFiles();
		
		for (int j = 0; j < trainFiles.length; ++j) {
			File trainFile = trainFiles[0];
			 
			String review = Files.readFromFile(trainFile, "ISO-8859-1");
			Classified classified = new Classified(review, classification);
			   ((ObjectHandler) class1).handle(classified); 
			
		}
 	}
	System.out.println("lmc");
	AbstractExternalizable.compileTo((Compilable) class1, new File("/home/vikas/dst/t/LMC/sender/t50/t/classifier50.txt"));

//System.out.println("done");
return "Data trained";
}
}