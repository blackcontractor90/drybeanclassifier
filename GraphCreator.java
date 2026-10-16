package gnnbeanfix;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class GraphCreator {
	// holds the data read in from the file
		private ArrayList<String> classification;
		private ArrayList<ArrayList<Double>> unstandardisedValues = new ArrayList<ArrayList<Double>>();
		
		private boolean[] dimensionsToShow = new boolean[8];

		private final String[] dimensionNames = {"Area", "Perimeter", "MajorAxisLength", "MinorAxisLength", "ConvexArea", 
				"EquivDiameter", "ShapeFactor", "ShapeFactor3"};
		// "Area", "Perimeter", "MajorAxisLength", "MinorAxisLength", "ConvexArea", "EquivDiameter", "ShapeFactor", "ShapeFactor3"
		private boolean showSample = true;
		
		private double[] sampleData = {0,0,0,0,0,0,0,0};
		
		public GraphCreator(){
			reloadData("TestBeanDataset1.txt");
		}
		
		public void reloadData(String filename){
			unstandardisedValues = BeanDataFileRead.readValues(filename);
			classification = BeanDataFileRead.readClassifications(filename);
		}
		
		public void setDimensionsToShow(boolean Area, boolean Perimeter, boolean MajorAxisLength,
											boolean MinorAxisLength, boolean ConvexArea, boolean EquivDiameter,
											boolean ShapeFactor, boolean ShapeFactor3){
			dimensionsToShow[0] = Area;
			dimensionsToShow[1] = Perimeter;
			dimensionsToShow[2] = MajorAxisLength;
			dimensionsToShow[3] = MinorAxisLength;
			dimensionsToShow[4] = ConvexArea;
			dimensionsToShow[5] = EquivDiameter;
			dimensionsToShow[6] = ShapeFactor;
			dimensionsToShow[7] = ShapeFactor3;
		}
		
		public void setSampleData(double[] sampleData){
			for(int x = 0; x < 8; x++)
				this.sampleData[x] = sampleData[x];
		}
		
		public void showSample(boolean show){
			showSample = show;
		}
		
		public JPanel lineChart(){
			XYSeriesCollection dataset = new XYSeriesCollection();
			
			// for all the classified sets we have
			for(int y = 0; y < unstandardisedValues.get(0).size(); y++){
				XYSeries series = new XYSeries("Test" + y);
				
				int t = 0;
				for(int x = 0; x < 8; x++)
					if(dimensionsToShow[x]){	
						series.add(t, unstandardisedValues.get(x).get(y));
						t++;
					}
					
				dataset.addSeries(series);
			}
			

			if(showSample){
				XYSeries testDataSeries = new XYSeries("Test sample");
				int t = 0;
				for(int x = 0; x < 8; x++)
					if(dimensionsToShow[x]){	
						testDataSeries.add(t, sampleData[x]);
						t++;
					}
				dataset.addSeries(testDataSeries);
			}
			
			JFreeChart chart = ChartFactory.createXYLineChart(
					"Classifications",      // chart title
					"Category",             // x axis label
					"Value",                // y axis label
					dataset,                // data
					PlotOrientation.VERTICAL,
					true,                   // include legend
					true,                   // tooltips
					false                   // urls
					);

			chart.setBackgroundPaint(Color.WHITE);

			XYPlot plot = chart.getXYPlot();
			plot.setBackgroundPaint(Color.lightGray);

			// Fix for LegendItem(String, Color) Undefined Compiler Crash
			LegendItemCollection legendItems = new LegendItemCollection();
			java.awt.geom.Rectangle2D.Double markerShape = new java.awt.geom.Rectangle2D.Double(-4, -4, 8, 8);
			
			legendItems.add(new LegendItem("Barbunya", "Barbunya", null, null, markerShape, Color.GREEN));
			legendItems.add(new LegendItem("Bombay", "Bombay", null, null, markerShape, Color.RED));
			legendItems.add(new LegendItem("Cali", "Cali", null, null, markerShape, Color.ORANGE));
			legendItems.add(new LegendItem("Dermason", "Dermason", null, null, markerShape, Color.BLUE));
			legendItems.add(new LegendItem("Horoz", "Horoz", null, null, markerShape, Color.CYAN));
			legendItems.add(new LegendItem("Seker", "Seker", null, null, markerShape, Color.MAGENTA));
			legendItems.add(new LegendItem("Sira ", "Sira ", null, null, markerShape, Color.PINK));
			
			if(showSample)
				legendItems.add(new LegendItem("Sample", "Sample", null, null, markerShape, Color.BLACK));
			
			plot.setFixedLegendItems(legendItems);
			

			ArrayList<String> dimensionsShown = new ArrayList<String>();
			for(int x = 0; x < 8; x++){
				if(dimensionsToShow[x])
					dimensionsShown.add(dimensionNames[x]);
			}
			String[] strArray = dimensionsShown.toArray(new String[dimensionsShown.size()]);
			
			SymbolAxis sa = new SymbolAxis("Measurement", strArray);
			plot.setDomainAxis(sa);
			
			boolean showShapes = (dimensionsShown.size() == 1);

			int seriesToColourCodeCount = dataset.getSeriesCount();
			if(showSample)
				seriesToColourCodeCount = dataset.getSeriesCount() - 1;
			
			XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
			
			for(int x = 0; x < seriesToColourCodeCount; x++){
				
				// color the series based on classification (Fixed structural overlap bug)
				if(classification.get(x).matches("Barbunya")) 
					renderer.setSeriesPaint(x, Color.GREEN);
			
				if(classification.get(x).matches("Bombay")) 
					renderer.setSeriesPaint(x, Color.RED);
				
				if(classification.get(x).matches("Cali")) 
					renderer.setSeriesPaint(x, Color.ORANGE);
				
				if(classification.get(x).matches("Dermason")) 
					renderer.setSeriesPaint(x, Color.BLUE);
				
				if(classification.get(x).matches("Horoz")) 
					renderer.setSeriesPaint(x, Color.CYAN);
				
				if(classification.get(x).matches("Seker")) 
					renderer.setSeriesPaint(x, Color.MAGENTA); // Fixed line rendering tracking color
				
				if(classification.get(x).matches("Sira")) 
					renderer.setSeriesPaint(x, Color.PINK); // Fixed line rendering tracking color
				

				renderer.setSeriesShapesVisible(x, showShapes);
			}
			

			if(showSample){
				renderer.setSeriesPaint(dataset.getSeriesCount() - 1, Color.WHITE);
				renderer.setSeriesShapesVisible(dataset.getSeriesCount() - 1, true);
			}
			
			plot.setRenderer(renderer);

			NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
			rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
			plot.getDomainAxis().setStandardTickUnits(NumberAxis.createIntegerTickUnits()); 
			
			ChartPanel chartPanel = new ChartPanel(chart);
			chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
			
			try {
				 ChartUtilities.saveChartAsJPEG(new File("Results.jpg"), chart, 1647, 857);
			} catch (IOException e) {
		           System.err.println("Error in chart generation " + e);
			}

			return chartPanel;
		}
}