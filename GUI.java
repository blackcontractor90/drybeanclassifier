package gnnbeanfix;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.MatteBorder;

public class GUI extends JFrame{

	private static final long serialVersionUID = 1L;
	//private Classifier cls = new Classifier("Stu_MentalStressRev.txt"); //data.txt
	private Classifier cls = new Classifier("TestBeanDataset1.txt");
	private GraphCreator gc = new GraphCreator();
	private JComboBox<String> KNNSelection;
	private JComboBox<String> KNNMethod;
	private String[] field = {"Area", "Perimeter", "MajorAxisLength", "MinorAxisLength", "ConvexArea", 
			"EquivDiameter", "ShapeFactor", "ShapeFactor3"};
	private EastPanel eastPanel;
	private ClassificationPanel classPanel;	
	private JPanel contentPanel;
	private JPanel chartPanel = new JPanel();	
	private JCheckBox[] checkBox = new JCheckBox[8];	
	private JCheckBoxMenuItem menuItemClassificationUpdate = new JCheckBoxMenuItem("Auto-Update Classification", true);	
	private JCheckBoxMenuItem menuItemShowSample = new JCheckBoxMenuItem("Show Sample", true);	
	private boolean dimensions[] = new boolean[8];
	
	public GUI() {
		initialize();	
	}
	
	private void initialize(){
		
		eastPanel = new EastPanel();
		chartPanel = gc.lineChart();
		classPanel = new ClassificationPanel();
		contentPanel = (JPanel) this.getContentPane();
		contentPanel.setLayout(new BorderLayout());
		contentPanel.add(eastPanel, BorderLayout.EAST);
		contentPanel.add(chartPanel, BorderLayout.CENTER);
		
		JPanel southPanel = new JPanel(new BorderLayout());
		southPanel.add(classPanel);
		southPanel.add(new JLabel("Go to File to turn off the Automatic Classification update/Show Sample on the graph." +
								  "This will stop the sanitation of inputs until you click classify"), BorderLayout.SOUTH);
		contentPanel.add(southPanel, BorderLayout.SOUTH);
		
		menuItemClassificationUpdate.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {

				if(menuItemClassificationUpdate.isSelected()){
					if(classPanel.setGCSampleData())
						classPanel.updateClassification();
				}else
					menuItemClassificationUpdate.setSelected(false);
			}
		});
		
		menuItemShowSample.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {				

				if(menuItemShowSample.isSelected())

					if(classPanel.setGCSampleData()){

						gc.showSample(true);
					

						updateGraph();
						
						GUI.this.validate();
					}else{

						menuItemShowSample.setSelected(false);
					}
				else{

					gc.showSample(false);

					updateGraph();
					
					GUI.this.validate();
				}
			}
		});
		
		JMenuItem menuItemExit = new JMenuItem("Exit");
		menuItemExit.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
		JMenuItem menuItemHelp = new JMenuItem("Show Help");
		menuItemHelp.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				new HelpGUI();
			}
		});
		
		JMenu menuFile = new JMenu("File");
		menuFile.add(menuItemClassificationUpdate);
		menuFile.add(menuItemShowSample);
		menuFile.add(menuItemExit);
		
		JMenu menuHelp = new JMenu("Help");
		menuHelp.add(menuItemHelp);
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(menuFile);
		menuBar.add(menuHelp);
		setJMenuBar(menuBar);
		

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle("Clustering Classification | BEAN TYPES |");
		pack();
		setVisible(true);
	}
	

	private void updateDimensions(){
		// tell the classifier which dimensions are to be checked
		for(int x = 0; x < 8; x++)
		dimensions[x] = checkBox[x].isSelected();
	}

	
	private void setGCDimensions(){
		// tell the graph creator which dimensions are to be shown
		gc.setDimensionsToShow(checkBox[0].isSelected(), checkBox[1].isSelected(), 
							   checkBox[2].isSelected(), checkBox[3].isSelected(), 
				               checkBox[4].isSelected(), checkBox[5].isSelected(), 
				               checkBox[6].isSelected(), checkBox[7].isSelected());
	}
	
	
	public void updateAccuracy(){

		if(KNNSelection.getSelectedIndex() != 0 && allCheckBoxesUnselected()){
			JOptionPane.showMessageDialog(contentPanel, "Classification does not work with i > 1 and no selected dimensions");
			KNNSelection.setSelectedIndex(0);
		}
			
		eastPanel.setAccuracyLabel(String.format("%.2f%%", cls.oneLeftOutTest(dimensions,KNNSelection.getSelectedIndex() + 1,KNNMethod.getSelectedIndex() + 1)));
	}
	
	public void updateGraph(){
		setGCDimensions();
		contentPanel.remove(chartPanel);
		chartPanel = gc.lineChart();
		contentPanel.add(chartPanel);
	}
	
	
	private boolean allCheckBoxesUnselected(){
		for(int x = 0; x < 8; x++)
			if(checkBox[x].isSelected())
				return false;
			
		return true;	
	}
	
	private class EastPanel extends JPanel{
		
		private static final long serialVersionUID = 1L;
		private JLabel measurementTitle = new JLabel("Measurements:", JLabel.CENTER);
		private Font measurementTitleFont = new Font("SansSerif", Font.BOLD, 22);
		
		private JLabel accuracy;
		private Font accuracyFont = new Font("SansSerif", Font.BOLD, 30);
		private JLabel accuracyTitle = new JLabel("Accuracy:", JLabel.CENTER);
		private Font accuracyTitleFont = new Font("SansSerif", Font.BOLD, 24);
		
		public EastPanel(){
		
			setLayout(new BorderLayout());
			setBorder(new MatteBorder(0,5,0,0, Color.BLACK));
			
			measurementTitle.setFont(measurementTitleFont);
			add(measurementTitle, BorderLayout.NORTH);
			
			add(new SettingsPanel());
			
			accuracy = new JLabel("",JLabel.CENTER);
			accuracy.setFont(accuracyFont);
			accuracyTitle.setFont(accuracyTitleFont);
			
			JPanel accuracyPanel = new JPanel(new GridLayout(2,1));
			accuracyPanel.setBorder(new MatteBorder(5,5,5,5, Color.YELLOW));
			
			accuracyPanel.add(accuracyTitle);
			accuracyPanel.add(accuracy);
			
			add(accuracyPanel, BorderLayout.SOUTH);
		}
		
		public void setAccuracyLabel(String text){
			accuracy.setText(text);
		}
	}
	
	private class SettingsPanel extends JPanel implements ItemListener{
			
		private static final long serialVersionUID = 1L;
			private boolean buttonPress = false;
			
			public SettingsPanel(){
				
				setLayout(new GridLayout(12,1));

				for(int x = 0; x < field.length; x++){
					checkBox[x] = new JCheckBox(field[x]);
					checkBox[x].addItemListener(this);
					add(checkBox[x]);
				}
							
				JPanel buttonPanel1 = new JPanel();
				JButton selectAll = new JButton("Select All");
				selectAll.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent arg0) {

						buttonPress = true;
						setAllCheckBoxes(true);

						buttonPress = false;

						if(menuItemClassificationUpdate.isSelected() || menuItemShowSample.isSelected()){

							if(classPanel.setGCSampleData()){
								
								updateAccuracy();
								updateGraph();	
		
								classPanel.updateClassification();
							}else{

								buttonPress = true;
								setAllCheckBoxes(false);
								buttonPress = false;
							}
						}else{
							updateAccuracy();
							updateGraph();
							GUI.this.validate();
						}
					}
				});
				buttonPanel1.add(selectAll);
				add(buttonPanel1);
				
				JPanel buttonPanel2 = new JPanel();
				JButton deselectAll = new JButton("De-Select All");
				deselectAll.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent arg0) {

						buttonPress = true;
						setAllCheckBoxes(false);

						buttonPress = false;

						updateAccuracy();
						updateGraph();
						GUI.this.validate();
					}
				});
				buttonPanel2.add(deselectAll);
				add(buttonPanel2);
				
				String[] knnI = {"KNN-1","KNN-2","KNN-3","KNN-4","KNN-5","KNN-6","KNN-7","KNN-8","KNN-9","KNN-10"};
				KNNSelection = new JComboBox<String>(knnI);
				KNNSelection.addActionListener(new ActionListener(){	
					@Override
					public void actionPerformed(ActionEvent arg0) {
						updateAccuracy();
						if(menuItemClassificationUpdate.isSelected())
							classPanel.updateClassification();
					}
				});
				add(KNNSelection);
				
				String[] knnOptions = {"KNN Normal", "KNN Weighted", "KNN Weighted-Squared", "Bayes Naive Classifier"};
				KNNMethod = new JComboBox<String>(knnOptions);
				KNNMethod.addActionListener(new ActionListener(){	
					@Override
					public void actionPerformed(ActionEvent arg0) {
						updateAccuracy();
						if(menuItemClassificationUpdate.isSelected())
							classPanel.updateClassification();
					}
				});
				add(KNNMethod);
				
			}
	
			@Override
			public void itemStateChanged(ItemEvent arg0) {			
				if(!buttonPress){
					updateDimensions();
					
					if(menuItemClassificationUpdate.isSelected() || menuItemShowSample.isSelected()){

						if(classPanel.setGCSampleData()){

							updateAccuracy();
							updateGraph();	
	
							classPanel.updateClassification();
							GUI.this.validate();
						} else{

							buttonPress = true;
							((JCheckBox) arg0.getItem()).setSelected(false);
							buttonPress = false;
						}
					} else{

						updateAccuracy();
						updateGraph();
						GUI.this.validate();
					}
					
					for(int x = 0; x < 8; x++)
						if(arg0.getSource() == checkBox[x])
							classPanel.changeLabelColour(x, checkBox[x].isSelected());
				}
			}
			
			public void setAllCheckBoxes(boolean selection){

				for(int x = 0; x < field.length; x++){
					checkBox[x].setSelected(selection);	
					classPanel.changeLabelColour(x, selection);
					updateDimensions();
				}
			}
	
		}

	private class ClassificationPanel extends JPanel{
		
		private static final long serialVersionUID = 1L;
	
		private JLabel[] labels = new JLabel[8];
		private JTextField[] testDataInput = new JTextField[8]; 
		private JButton classify = new JButton("Classify");
		private JLabel result = new JLabel("----", JLabel.CENTER);
		private double[] testData = new double[8];
		
		public ClassificationPanel(){
			
			setLayout(new GridLayout(2,9));
			setBorder(new MatteBorder(5,0,0,0, Color.BLACK));			
			for(int x = 0; x < 8; x++){
				labels[x] = new JLabel(field[x]);
				add(labels[x]);
			}
			
			classify.addActionListener(new ActionListener(){
	
				public void actionPerformed(ActionEvent e) {

					if(setGCSampleData()){
						updateClassification();
					}
					
					if(menuItemShowSample.isSelected()){
						updateGraph();
						GUI.this.validate();
					}
				}
			});
			

			add(classify);
			
			for(int x = 0; x < 8; x++){
				testDataInput[x] = new JTextField("0", 5);
				add(testDataInput[x]);
			}
			
			add(result);	
		}


	public void changeLabelColour(int x, boolean red){
		if(red)
			labels[x].setForeground(Color.RED);
		else
			labels[x].setForeground(Color.BLACK);
	}
	
	
	public boolean setGCSampleData(){

		for(int x = 0; x < 8; x++){

			if(checkBox[x].isSelected())

				try{
					testData[x] = (Double.valueOf(testDataInput[x].getText()));
				}catch(NumberFormatException nf){
					JOptionPane.showMessageDialog(contentPanel, "Please enter a valid number in all required fields");
					result.setText("----");
					return false;
				}
			
		}
		gc.setSampleData(testData);
		return true;
	}
	
	public void updateClassification(){	
			contentPanel.remove(chartPanel);
		    gc.setSampleData(testData);
		    chartPanel = gc.lineChart();
		    contentPanel.add(chartPanel);
			result.setText(cls.classify(dimensions, testData, KNNSelection.getSelectedIndex() + 1, KNNMethod.getSelectedIndex()+1));
	}
}
}
