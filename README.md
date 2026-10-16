# KNN and Naive Bayes Weighted Polling for Dry Bean Classification

Source code supporting:

> Morsidi, F. (2026). Hybrid machine learning merging KNN and Naive Bayes weighted polling for dry beans multiclass classification. *ASM Science Journal*, 21(2), 1-18. https://doi.org/10.32802/asmscj.2026.66

## What this is

A Java implementation of a K-Nearest Neighbours classifier with four polling methods, evaluated on the Dry Bean dataset (13,611 samples, 7 classes, 8 morphological features) with leave-one-out testing and exhaustive feature-subset search.

| Method | Polling rule |
|---|---|
| 1 | Normal: each neighbour contributes one vote |
| 2 | Weighted: closer neighbours contribute more |
| 3 | Weighted-Squared: closer neighbours contribute quadratically more |
| 4 | Naive Bayes style: votes normalised by class frequency |

Methods 1-3 correspond to Table 3 of the paper.

## Files

- `src/gnnbeanfix/Classifier.java` - classifier, polling methods, leave-one-out test, feature-subset search
- `src/gnnbeanfix/BeanDataFileRead.java`, `ArrayListMath.java` - data loading and helpers
- `src/gnnbeanfix/GUI.java`, `HelpGUI.java`, `GraphCreator.java` - Swing interface and parallel-coordinates chart (requires JFreeChart)
- `src/gnnbeanfix/Main.java` - launches the GUI; also holds helper methods for batch accuracy searches
- `src/gnnbeanfix/Reproduce.java` - reproduces the accuracy values reported in the paper
- `TestBeanDataset1.txt` - the dataset in the format the code reads

`Classifier`, `BeanDataFileRead`, `ArrayListMath` and `Reproduce` need only the standard Java library. The GUI and chart classes need JFreeChart on the classpath.

## Reproducing the reported accuracies

From the project root (the folder containing `TestBeanDataset1.txt`):

    javac -d bin src/gnnbeanfix/Classifier.java src/gnnbeanfix/BeanDataFileRead.java src/gnnbeanfix/ArrayListMath.java src/gnnbeanfix/Reproduce.java
    java -cp bin gnnbeanfix.Reproduce

Setup: leave-one-out, k = 3, polling method 3. Expected output (each line takes roughly 20-60 seconds):

    All 8 features         accuracy = 88.54%  (paper: 88.54%)
    Without Perimeter      accuracy = 85.67%  (paper: 85.67%)
    Without EquivDiameter  accuracy = 88.58%  (paper: 88.58%)

Feature order in the code: Area, Perimeter, MajorAxisLength, MinorAxisLength, ConvexArea, EquivDiameter, ShapeFactor1, ShapeFactor3.

## Notes

- The reported accuracies were reproduced with OpenJDK 21.
- The code implements the vote-tally polling methods above. It does not train a local Naive Bayes model per test point or derive classifier weights by cross-validation.
- Figure 4 in the paper labels Perimeter as included in every iteration. Running this code, the 85.67% result arises when Perimeter is excluded.

## Data

The Dry Bean dataset is by Koklu and Ozkan (2020), *Computers and Electronics in Agriculture*, 174, 105507, https://doi.org/10.1016/j.compag.2020.105507. It is distributed through the UCI Machine Learning Repository; check its licence terms before redistributing the data file.

## Licence

(Choose a licence for the code, for example MIT, and state it here.)
