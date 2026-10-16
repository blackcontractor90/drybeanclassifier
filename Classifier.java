package gnnbeanfix;


import java.util.ArrayList;

public class Classifier {
	private int noDimensions = 8;

	private int testSetSize;

	// Area Perimeter MajorAxisLength MinorAxisLength ConvexArea EquivDiameter ShapeFactor1 ShapeFactor3 Class

	// number of points for each classification
	// order: Barbunya, Bombay, Cali, Dermason, Horoz, Seker, Sira
	private static final int NO_CLASSES = 7;
	private int[] noOfEachClass = new int[NO_CLASSES];

	// stores the bayes naive value necessary
	private double[] tallyOverTotal;

	private ArrayList<String> classification;
	private ArrayList<ArrayList<Double>> unstandardisedTestSet;

	// information about each dimension
	private double[] meanValues = new double[noDimensions];
	private double[] sdValues = new double[noDimensions];

	// Holds all the standardised values (Standardised test set)
	private ArrayList<ArrayList<Double>> standardisedTestSet = new ArrayList<ArrayList<Double>>();

	// the sample to be classified
	private double[] unstandardisedSampleData = new double[noDimensions];
	private double[] standardisedSampleData = new double[noDimensions];


	private boolean reset = true;


	public Classifier(String filename) {

		unstandardisedTestSet = BeanDataFileRead.readValues(filename);
		classification = BeanDataFileRead.readClassifications(filename);
		testSetSize = unstandardisedTestSet.get(0).size();

		resetClassifierAllDimensions();

		countClassifications();

	}


	private void countClassifications() {

		for(int x = 0; x < NO_CLASSES; x++)
			noOfEachClass[x] = 0;

		for(String c: classification){

			/* Barbunya Bombay Cali Dermason Horoz Seker Sira */
			if(c.matches("Barbunya")){
				noOfEachClass[0]++;
				continue;
			}

			if(c.matches("Bombay")){
				noOfEachClass[1]++;
				continue;
			}

			if(c.matches("Cali")){
				noOfEachClass[2]++;
				continue;
			}

			if(c.matches("Dermason")){
				noOfEachClass[3]++;
				continue;
			}

			if(c.matches("Horoz")){
				noOfEachClass[4]++;
				continue;
			}

			if(c.matches("Seker")){
				noOfEachClass[5]++;
				continue;
			}

			if(c.matches("Sira")){
				noOfEachClass[6]++;
				continue;
			}
		}
	}


	public String classify(boolean[] dimensions, double[] testData, int i, int method){

			if(i > testSetSize || i <= 0){
				System.out.println("Incorrect i -  Max: " + (testSetSize - 1) + " Min: 1");
				return null;
			}

			if(method < 1 || method > 4){
				System.out.println("Please enter a valid method 1-4");
				return null;
			}

			// set the sample as the given values
			for(int x = 0; x < noDimensions; x++)
				unstandardisedSampleData[x] = testData[x];

			return classify(dimensions, i, method);
		}


	private String classify(boolean[] dimensions, int i, int method){

		standardiseSample();

		DistClass[] neighbours = new DistClass[i];

		for(int x = 0; x < i; x++)
			neighbours[x] = new DistClass(Double.MAX_VALUE, "Barbunya");


		double distance;

		for(int y = 0; y < testSetSize; y++){
			distance = 0;

			for(int n = 0; n < noDimensions; n++){
				if(dimensions[n])
					distance += Math.pow(Math.abs(standardisedSampleData[n] - standardisedTestSet.get(n).get(y)), 2);
			}

			// if one of the i closest points
			if(distance < neighbours[i-1].distance){
				int x = i-1;

				// loop through and find which position it belongs in
				while(x > 0){

					// if the next distance is smaller, then x is the position for new distance
					if(distance >= neighbours[x-1].distance)
						break;
					else
						x--;
				}

				// move all neighbours further away than the one just found one place out
				for(int j = i - 1; j > x; j--)

					neighbours[j] = neighbours[j-1];

				// add new neighbour in correct position
				neighbours[x] = new DistClass(distance, classification.get(y));

			}

		}


		int maxScore = i;

		if(i == 1)

			return neighbours[0].classification;

		else{

			int[] tally = new int[NO_CLASSES];

			for(int x = 0; x < i; x++){

				// get the xth nearest classified set's classification
				String result = neighbours[x].classification;

				addToTally(tally, result, method, maxScore, x);

			}

			int winner;

			if(method == 4){

				tallyOverTotal = new double[NO_CLASSES];

				for(int x = 0; x < NO_CLASSES; x++)
					tallyOverTotal[x] = (double) tally[x] / noOfEachClass[x];

				winner = arrayWinner(tallyOverTotal);

			}else{
				// check if there is an outright winner in the tally
				winner = arrayWinner(tally);
			}

			// i-1 is the index of the last neighbour we checked
			int x = i - 1;


			while(winner == -1 && x >= 0){

				String result = neighbours[x].classification;

				addToTally(tally, result, method, maxScore, x);

				if(method == 4){
					for(int c = 0; c < NO_CLASSES; c++)
						tallyOverTotal[c] = (double) tally[c] / noOfEachClass[c];

					winner = arrayWinner(tallyOverTotal);
				}else
					winner = arrayWinner(tally);

				x--;
			}

			// fallback: if still tied after exhausting all neighbours,
			// default to the single nearest neighbour's class
			if(winner == -1)
				winner = classIndexFromName(neighbours[0].classification);

			return classNameFromIndex(winner);
		}
	}


	/**
	 * Updates the tally array (and, for method 4, tallyOverTotal) for the class
	 * matched by 'result', according to the selected polling method.
	 *
	 * Higher tally values indicate more support for a class; arrayWinner()
	 * selects the class with the highest tally.
	 *
	 * method 1: Normal      - each neighbour contributes +1
	 * method 2: Weighted    - closer neighbours contribute more: +(maxScore - x)
	 * method 3: Weighted^2  - closer neighbours contribute even more: +(maxScore - x)^2
	 * method 4: Naive Bayes - raw votes accumulated (+1), then normalised by class frequency
	 */
	private void addToTally(int[] tally, String result, int method, int maxScore, int x){

		int classIndex = classIndexFromName(result);

		if(classIndex == -1)
			return;

		switch(method){
			case 1: tally[classIndex]++; break;
			case 2: tally[classIndex] += (maxScore - x); break;
			case 3: tally[classIndex] += (int) Math.pow(maxScore - x, 2); break;
			case 4: tally[classIndex]++; break; // raw tally accumulated; normalised separately
		}
	}


	/**
	 * Maps a class name string to its index (0-6).
	 * Order: Barbunya, Bombay, Cali, Dermason, Horoz, Seker, Sira
	 */
	private int classIndexFromName(String name){

		if(name.matches("Barbunya")) return 0;
		if(name.matches("Bombay"))   return 1;
		if(name.matches("Cali"))     return 2;
		if(name.matches("Dermason")) return 3;
		if(name.matches("Horoz"))    return 4;
		if(name.matches("Seker"))    return 5;
		if(name.matches("Sira"))     return 6;

		return -1;
	}


	/**
	 * Maps a winning index (0-6) back to its class name.
	 */
	private String classNameFromIndex(int index){

		switch(index){
			case 0: return "Barbunya";
			case 1: return "Bombay";
			case 2: return "Cali";
			case 3: return "Dermason";
			case 4: return "Horoz";
			case 5: return "Seker";
			case 6: return "Sira";
			default: return null;
		}
	}


	private void standardiseSample() {
		for(int x = 0; x < noDimensions; x++)
			standardisedSampleData[x] = (unstandardisedSampleData[x] - meanValues[x]) / sdValues[x];
	}


	private class DistClass{
		double distance;
		String classification;

		DistClass(double d, String c){
			distance = d;
			classification = c;
		}
	}

	/**
	 * Finds the index of the largest value in an int[] tally array.
	 * Returns -1 if there is a tie for the highest value (no outright winner).
	 */
	private int arrayWinner(int[] tally){

		boolean equalHighestCount = false;

		int winner = 0;

		for(int x = 1; x < NO_CLASSES; x++){
			if(tally[x] > tally[winner]){
				winner = x;
				// current outright winner
				equalHighestCount = false;
			}else
				if(tally[x] == tally[winner])
					// the highest value so far is duplicated
					equalHighestCount = true;
		}

		if(equalHighestCount)
			// no outright highest winner
			return -1;
		else
			// else an outright winner
			return winner;
	}


	/**
	 * Finds the index of the largest value in a double[] tally array.
	 * Returns -1 if there is a tie for the highest value (no outright winner).
	 */
	private int arrayWinner(double[] tally){

		boolean equalHighestProbability = false;

		int winner = 0;

		for(int x = 1; x < NO_CLASSES; x++){
			if(tally[x] > tally[winner]){
				winner = x;

				equalHighestProbability = false;
			}else
				if(tally[x] == tally[winner])

					equalHighestProbability = true;
		}

		if(equalHighestProbability)

			return -1;
		else
			return winner;
	}


	public void findBestAccuracy(int iMin, int iMax, int method){

		if(iMin > iMax){
			System.out.println("iMin should be <= to iMax");
			return;
		}

		if(iMin <= 0){
			System.out.println("iMin should be >= 1");
			return;
		}

		if(iMax > (testSetSize - 1)){
			System.out.println("iMax should be <= " + (testSetSize - 1));
			return;
		}

		if(method < 1 || method > 4){
			System.out.println("Please enter a valid method 1-4");
			return;
		}

		reset = false;

		double percentage = 0;
		int index = 0;
		int KNN = 0;

		int noCombinations = (int) Math.pow(2, noDimensions);

		for(int i = iMin; i <= iMax; i++){

			boolean[] dimensions = {false,false,false,false,false,false,false,false};

 			for(int x = 1; x <= noCombinations - 1; x++){

 				if(x == 128)
					dimensions[0] = true;

				if(x % 64 == 0)
					dimensions[1] = !dimensions[1];

				if(x % 32 == 0)
					dimensions[2] = !dimensions[2];

				if(x % 16 == 0)
					dimensions[3] = !dimensions[3];

				if(x % 8 == 0)
					dimensions[4] = !dimensions[4];

				if(x % 4 == 0)
					dimensions[5] = !dimensions[5];

				if(x % 2 == 0)
					dimensions[6] = !dimensions[6];

				dimensions[7] = !dimensions[7];

				double accuracy = oneLeftOutTest(dimensions, i,method);

				if(accuracy > percentage){
					percentage = accuracy;
					index = x;
					KNN = i;
				}
			}
		}

		String optimalDimensions = "Dimensions: ";
		// Area Perimeter MajorAxisLength MinorAxisLength ConvexArea EquivDiameter ShapeFactor1 ShapeFactor3 Class
		if(index >= 127)
			optimalDimensions += "Area/";

		if((index / 64) % 2 != 0)
			optimalDimensions += "Perimeter/";

		if((index / 32) % 2 != 0)
			optimalDimensions += "MajorAxisLength/";

		if((index / 16) % 2 != 0)
			optimalDimensions += "MinorAxisLength/";

		if((index / 8) % 2 != 0)
			optimalDimensions += "ConvexArea/";

		if((index / 4) % 2 != 0)
			optimalDimensions += "EquivDiameter/";

		if((index / 2) % 2 != 0)
			optimalDimensions += "ShapeFactor1/";

		if(index % 2 != 0)
			optimalDimensions += "ShapeFactor3/";


		System.out.printf("Best accuracy achieved: %.2f \n",percentage);
		System.out.println("KNN-" + KNN);

		System.out.println(optimalDimensions);

		reset = true;

		resetClassifierAllDimensions();
	}


	public void findAverageAccuracy(int i, int method){

		if(i > testSetSize || i <= 0){
			System.out.println("Incorrect i -  Max: " + (testSetSize - 1) + " Min: 1");
			return;
		}

		if(method < 1 || method > 4){
			System.out.println("Please enter a valid method 1-4");
			return;
		}

		reset = false;

		// stores the percentages
		ArrayList<Double> percentages = new ArrayList<Double>();

		boolean[] dimensions = {false,false,false,false,false,false,false,false};

		// total number of configurations of the dimensions
		int noCombinations = (int) Math.pow(2, noDimensions);

		// runs through all 2^n combinations (Excluding all false)
		for(int x = 1; x <= noCombinations - 1; x++){

			if(x == 128)
				dimensions[0] = true;

			if(x % 64 == 0)
				dimensions[1] = !dimensions[1];

			if(x % 32 == 0)
				dimensions[2] = !dimensions[2];

			if(x % 16 == 0)
				dimensions[3] = !dimensions[3];

			if(x % 8 == 0)
				dimensions[4] = !dimensions[4];

			if(x % 4 == 0)
				dimensions[5] = !dimensions[5];

			if(x % 2 == 0)
				dimensions[6] = !dimensions[6];

			dimensions[7] = !dimensions[7];

			percentages.add(oneLeftOutTest(dimensions, i,method));

		}

		System.out.printf("Average percentage for KNN-%d Method: %d is %.2f \n", i, method,ArrayListMath.mean(percentages));

		reset = true;

		resetClassifierAllDimensions();

	}

	public double oneLeftOutTest(boolean[] dimensions, int i, int method){


	    int correct = 0;

		for(int sampleNo = 0; sampleNo < testSetSize; sampleNo++){

			String expectedResult = classification.get(sampleNo);

			getSample(sampleNo);

			removeSample(sampleNo);

			calculateMeanAndSDValues(dimensions);
			standardiseTestSet(dimensions);

			if(classify(dimensions,i,method).matches(expectedResult))
				correct++;

			replaceSampleValues(sampleNo, expectedResult);
		}


		if(reset)

			resetClassifierAllDimensions();


		return ((double) correct / unstandardisedTestSet.get(0).size()) * 100;
	}

	/**
	 * @param dimensions
	 */
	private void standardiseTestSet(boolean[] dimensions) {
		standardisedTestSet = new ArrayList<ArrayList<Double>>();

		// scale each list of data
		for(int x = 0; x < noDimensions; x++)
			// no need to do the calculations for dimensions we are not concerned about
			if(dimensions[x])
				standardisedTestSet.add(ArrayListMath.scaleDataByStandardScore(unstandardisedTestSet.get(x), meanValues[x], sdValues[x]));
			else
				standardisedTestSet.add(new ArrayList<Double>());
	}


	/**
	 * @param dimensions
	 */
	private void calculateMeanAndSDValues(boolean[] dimensions) {

		for(int x = 0; x < noDimensions; x++){

			if(dimensions[x]){
				meanValues[x] = ArrayListMath.mean(unstandardisedTestSet.get(x));
				sdValues[x] = ArrayListMath.standardDeviation(unstandardisedTestSet.get(x), meanValues[x]);
			}
		}
	}


	/**
	 * assigns the sample from the test set to the double[] for the sample data
	 * @param sampleNo The test set index of the sample to be set as sample data
	 */
	private void getSample(int sampleNo){
		for(int x = 0; x < noDimensions; x++)
			unstandardisedSampleData[x] = unstandardisedTestSet.get(x).get(sampleNo);
	}


	private void removeSample(int sampleNo){
		for(int x = 0; x < unstandardisedTestSet.size(); x++)
			unstandardisedTestSet.get(x).remove(sampleNo);

		String classif = classification.get(sampleNo);

		classification.remove(sampleNo);

	    testSetSize --;

		int classIndex = classIndexFromName(classif);
		if(classIndex != -1)
			noOfEachClass[classIndex]--;
	}

	private void replaceSampleValues(int sampleNo, String expectedResult){
		// replace values at index they were removed from
		for(int x = 0; x < noDimensions; x++)
			unstandardisedTestSet.get(x).add(sampleNo, unstandardisedSampleData[x]);

		// replace the classification at the same index
		classification.add(sampleNo, expectedResult);

		testSetSize++;

		// maintain count of each classification; Barbunya Bombay Cali Dermason Horoz Seker Sira
		int classIndex = classIndexFromName(expectedResult);
		if(classIndex != -1)
			noOfEachClass[classIndex]++;
	}



	private void resetClassifierAllDimensions() {
		calculateMeanAndSDValuesAllDimensions();
		standardiseTestSetAllDimensions();
	}

	private void standardiseTestSetAllDimensions() {

		// new list of arraylists created so unstandardised double objects are not affected and still accessible
		standardisedTestSet = new ArrayList<ArrayList<Double>>();

		// scale each list of data
		for(int x = 0; x < noDimensions; x++)
				standardisedTestSet.add(ArrayListMath.scaleDataByStandardScore(unstandardisedTestSet.get(x), meanValues[x], sdValues[x]));

	}

	private void calculateMeanAndSDValuesAllDimensions() {
		// calculate the mean and standard deviation for each dimension
		for(int x = 0; x < noDimensions; x++){
			meanValues[x] = ArrayListMath.mean(unstandardisedTestSet.get(x));
			sdValues[x] = ArrayListMath.standardDeviation(unstandardisedTestSet.get(x), meanValues[x]);
		}

	}
}