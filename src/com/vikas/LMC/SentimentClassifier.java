package com.vikas.LMC;

import java.io.File;
import java.io.IOException;

import com.aliasi.classify.ConditionalClassification;
import com.aliasi.classify.LMClassifier;
import com.aliasi.util.AbstractExternalizable;

public class SentimentClassifier {  
    String[] categories;  
    LMClassifier class1;  
    public SentimentClassifier() {  
    try { 
    	System.out.println("cons");
       class1= (LMClassifier) AbstractExternalizable.readObject(new File("/home/vikas/dst/t/LMC/sender/t50/t/classifier50.txt"));  
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
   /* public static void main(String []args)
    {
    	SentimentClassifier s =new SentimentClassifier();
    }
    
    */
 }  