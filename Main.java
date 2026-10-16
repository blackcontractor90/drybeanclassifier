package gnnbeanfix;


public class Main {

		/**
		 * @param args
		 */
		Classifier cls = new Classifier("TestBeanDataset1.txt");
		//Classifier cls = new Classifier("Stu_MentalStressRev.txt"); 
		
		
		public static void main(String[] args) {
			// TODO Auto-generated method stub
			Main t = new Main();
			GUI g = new GUI();

		}
		
		public void printOutAverages(){
			for(int i = 1; i <= 10; i++){
				cls.findAverageAccuracy(i, 1);
				cls.findAverageAccuracy(i, 2);
				cls.findAverageAccuracy(i, 3);
				cls.findAverageAccuracy(i, 4);
			}
		}
		
		//prints out the best accuracy found by any i/method combination between and including the two i values in the for loop.
		public void printOutBest(){
				cls.findBestAccuracy(1,10, 1);
				cls.findBestAccuracy(1,10, 2);
				cls.findBestAccuracy(1,10, 3);
				cls.findBestAccuracy(1,10, 4);
		}
		
		
		//prints out the best accuracy found for each i/method combination and the dimensions subset producing each accuracy.
		public void printOutBestsPerI(){
			for(int i = 1; i <= 10; i++){
				cls.findBestAccuracy(i,i, 1);
				cls.findBestAccuracy(i,i, 2);
				cls.findBestAccuracy(i,i, 3);
				cls.findBestAccuracy(i,i, 4);
			}
	}

}
