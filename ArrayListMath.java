package gnnbeanfix;


import java.util.ArrayList;

public class ArrayListMath {

	
	public static double mean(ArrayList<Double> values){
		double total = 0;
		
		if(values.size() != 0){
			for(double value : values)
				total += value;
		
			return total/values.size();	
		}else{
			System.out.println("ArrayList size is 0. Mean could not be calculated");
			return 0;
		}
	}
	
	public static double standardDeviation(ArrayList<Double> values){

		double mean = ArrayListMath.mean(values);
		
		if(values.size() != 0){
		
			// total of (x - mean)^2
			double total = 0;
			
			for(double value: values)
				total += Math.pow(value - mean, 2);
			
			return Math.sqrt(total / (values.size() - 1));	
		}else{
			System.out.println("ArrayList size is 0. SD could not be calculated");
			return 0;
		}
	}
	
	public static double standardDeviation(ArrayList<Double> values, double mean){

		if(values.size() != 0){
		
			// total of (x - Mean)^2
			double total = 0;
			
			for(double value: values)
				total += Math.pow(value - mean, 2);
			
			return Math.sqrt(total / (values.size() - 1));	
		}else{
			System.out.println("ArrayList size is 0. SD could not be calculated");
			return 0;
		}
	}
	
	public static ArrayList<Double>scaleDataByStandardScore(ArrayList<Double> values){
		// scale by the process of ((x - mean) / sd)
		if(values.size() != 0){	
			double mean = ArrayListMath.mean(values);
			double sd = ArrayListMath.standardDeviation(values, mean);
			
			// create a new array list so we do not alter the values of the list passed in
			ArrayList<Double> standValues = new ArrayList<Double>();
			
			for(double value : values)
				standValues.add((value - mean) / sd);		
			
			return standValues;
		}else{
			System.out.println("ArrayList to scale is empty");
			return null;
		}
	}
	
	public static ArrayList<Double>scaleDataByStandardScore(ArrayList<Double> values, double mean,double sd){
		if(values.size() != 0){
			// create a new array list so we do not alter the values of the list passed in
			ArrayList<Double> standValues = new ArrayList<Double>();
			
			for(double value : values)
				standValues.add((value - mean) / sd);		
			
			return standValues;
		}else{
			System.out.println("ArrayList to scale is empty");
			return new ArrayList<Double>();
		}
	}
	
}
