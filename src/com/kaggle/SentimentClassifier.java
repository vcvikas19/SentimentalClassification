package com.kaggle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.aliasi.classify.ConditionalClassification;
import com.aliasi.classify.LMClassifier;
import com.aliasi.util.AbstractExternalizable;

public class SentimentClassifier {  
	
	static int p,n=0;
    String[] categories;  
    LMClassifier class1;  
    public SentimentClassifier() {  
    try { 
    	System.out.println("cons");
       class1= (LMClassifier) AbstractExternalizable.readObject(new File("/home/vikas/dst/1/kagglespecial/kaggle/T/classifierk.txt"));  
       categories = class1.categories();  
       System.out.println("cons1");
    }  
    catch (ClassNotFoundException e) {  
       e.printStackTrace();  
    }  
    catch (IOException e) {  
       e.printStackTrace();  
    } 
    }  
    public String classify(String text) {  
    ConditionalClassification classification = class1.classify(text);  
     return classification.bestCategory();  
    }  

    public static void main(String []args) throws ClassNotFoundException, IOException
    
    {
    	Trainer t = new Trainer();
        t.trainModel();
    	SentimentClassifier s =new SentimentClassifier();
    	
    	 BufferedReader br = null;

			

				String sCurrentLine;
//SentimentClassifier sm = new SentimentClassifier();
				br = new BufferedReader(new FileReader("/home/vikas/dst/1/kagglespecial/kaggle/testdata.txt"));

				while ((sCurrentLine = br.readLine()) != null) 
				{
					System.out.println("*"+sCurrentLine);
				String r=	s.classify(sCurrentLine);
				if(r.equals("pos"))
				{
					p++;
				}
				else
				{
					n++;
				}
				}
    	//
				System.out.println(p+"and"+n);
			}
			
}
 