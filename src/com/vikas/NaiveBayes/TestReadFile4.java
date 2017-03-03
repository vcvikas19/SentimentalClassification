package com.vikas.NaiveBayes;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TestReadFile4{

	public static void main(String args[]) {

		String fileName = "/home/vikas/Desktop/inputd3/testgold.txt";
         long k =0;
		try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {

			String line;
			while ((line = br.readLine()) != null) {
				k++;
				System.out.println(k+"*"+line);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}