package gnnbeanfix;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

public class BeanDataFileRead {
	private static int noOfDimensions = 8;
	
	public static ArrayList<ArrayList<Double>> readValues(String filename){
		
		ArrayList<ArrayList<Double>> values = new ArrayList<ArrayList<Double>>();
		
		// create the array lists
		for(int x = 0; x < noOfDimensions; x++)
			values.add(new ArrayList<Double>());
				
		Scanner s;
		try {
			s = new Scanner(new BufferedReader(new FileReader(filename)));
			
			s.nextLine(); 
			
			while (s.hasNext()) {
		
				for(ArrayList<Double> list : values)
					list.add(s.nextDouble());
				
				// skip the classification 
				s.next();
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}	
				
		return values;
	}
	
	public static ArrayList<String> readClassifications(String filename){
		
		ArrayList<String> classification = new ArrayList<String>();
		
		Scanner s;
		try {
			s = new Scanner(new BufferedReader(new FileReader(filename)));
			
			s.nextLine();  // skips column titles
			
			while (s.hasNext()) {
				// skip the double values
				for(int x = 0; x < noOfDimensions; x++)
					s.nextDouble();
				
				// read in the classification
				classification.add(s.next());
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}	
		
		return classification;
	}
}
