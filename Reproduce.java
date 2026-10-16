package gnnbeanfix;

/**
 * Reproduces the accuracy values reported in:
 * Morsidi, F. (2026). Hybrid machine learning merging KNN and Naive Bayes
 * weighted polling for dry beans multiclass classification.
 * ASM Science Journal, 21(2), 1-18. https://doi.org/10.32802/asmscj.2026.66
 *
 * Setup: TestBeanDataset1.txt (13,611 samples), leave-one-out evaluation,
 * k = 3 neighbours, polling method 3 (Weighted-Squared).
 *
 * Feature order: Area, Perimeter, MajorAxisLength, MinorAxisLength,
 * ConvexArea, EquivDiameter, ShapeFactor1, ShapeFactor3.
 *
 * Run from the project root (the folder containing TestBeanDataset1.txt).
 * Each configuration takes roughly 20-60 seconds.
 */
public class Reproduce {

	private static final int K = 3;
	private static final int METHOD = 3;

	public static void main(String[] args) {
		run("All 8 features",        new boolean[] {true, true, true, true, true, true, true, true},     88.54);
		run("Without Perimeter",     new boolean[] {true, false, true, true, true, true, true, true},    85.67);
		run("Without EquivDiameter", new boolean[] {true, true, true, true, true, false, true, true},    88.58);
	}

	private static void run(String label, boolean[] dimensions, double reported) {
		// a fresh classifier per configuration keeps each run independent
		Classifier cls = new Classifier("TestBeanDataset1.txt");
		double accuracy = cls.oneLeftOutTest(dimensions, K, METHOD);
		System.out.printf("%-22s k=%d method=%d  accuracy = %.2f%%  (paper: %.2f%%)%n",
				label, K, METHOD, accuracy, reported);
	}
}
