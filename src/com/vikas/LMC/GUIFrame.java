package com.vikas.LMC;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import twitter4j.TwitterException;


public class GUIFrame extends JFrame implements ActionListener{
	public static int pc=0;
	public static int nc=0;
	static double pp;
	static double np;
	private JScrollPane scrollPane; //ScrollPane used to display TestAreas
	//private JTextArea outputJTextArea; //Text area to display text
	private JTextPane textPane; //Text area to display text
	private JPanel panel1; //Panel that displays other GUI components
	private JTextField textField; //Where user inputs term to search twitter for
	private JButton searchButton; //Button to press to start search for tweets 
	private JLabel positiveLabel; 
	private JLabel positivePercentLabel; //Display percent of tweets that are positive
	private JLabel negativeLabel; 
	private JLabel negativePercentLabel; //Display percent of tweets that are negative
	private Color greenColor = new Color(18,149,18);
	
	/*
	 * Constructor for GUIFrame
	 * Sets all the GUI components
	 */
	public GUIFrame(){
		super("Frame");
	
		/*
		//Initialize text area
		outputJTextArea = new JTextArea(10,30);
		outputJTextArea.setLineWrap( true );
	    outputJTextArea.setEditable( false );
	    outputJTextArea.setBackground( Color.WHITE );
	    outputJTextArea.setForeground( Color.BLACK );
	    */
		
	    //Initialize text pane
	    textPane = new JTextPane();
	    textPane.setEditable( false );
	    textPane.setBackground( Color.orange);
	    textPane.setForeground( Color.BLACK );
	    
	    //Attaches output of JTextArea to JScrollPane
		scrollPane = new JScrollPane(textPane);
		
		//Labels
		positiveLabel = new JLabel("Positive");
		positiveLabel.setForeground(greenColor);
		positivePercentLabel = new JLabel("");
		positivePercentLabel.setForeground(greenColor);
		negativeLabel = new JLabel("Negative");
		negativeLabel.setForeground(Color.RED);
		negativePercentLabel = new JLabel("");
		negativePercentLabel.setForeground(Color.RED);
		
		//TextField
		textField = new JTextField(20);
		textField.addActionListener(this);

		//Search button
		searchButton = new JButton("Search");
		searchButton.addActionListener(this);
		
		//Create panel to add GUI components to JPanel
		panel1 = new JPanel();
		panel1.add(positiveLabel);
		panel1.add(positivePercentLabel);
		panel1.add(negativeLabel);
		panel1.add(negativePercentLabel);
		panel1.add(textField);
		panel1.add(searchButton);
		
		//Add ScrollPane and Panel to JFrame
		setLayout( new BorderLayout() );
		add(scrollPane, BorderLayout.CENTER);
		add(panel1, BorderLayout.SOUTH);
	}//end constructor
	
	/*
	 * Required implementation of ActionListener interface
	 */
	@Override
	public void actionPerformed(ActionEvent e){
		// TODO Auto-generated method stub
		textPane.setText("");
		if(e.getSource() == searchButton){
			try {
				try {
					searchAndAnalyze();
				} catch (BadLocationException | InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} catch (TwitterException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}//end try
		}//end if
	}//end method
	
	/*
	 * Retrieves tweets, analyzes sentiment and displays results in GUI
	 */
	private void searchAndAnalyze() throws TwitterException, IOException, BadLocationException, InterruptedException{
		double positiveCount = 0;
		double negativeCount = 0;
		
		//Get Tweets given search term
		TwitterData twitter = new TwitterData();
		String searchTerm = textField.getText();
		searchTerm = searchTerm+"&lang:en";
		ArrayList<String> tweets = twitter.search(searchTerm);
		
		//Analyze sentiment of tweets 
		//TestData test = new TestData();
		int k = tweets.size();
		SentimentClassifier sm = new SentimentClassifier();
		StyledDocument doc = textPane.getStyledDocument();
        javax.swing.text.Style style = textPane.addStyle("I'm a Style", null);
    //    DecimalFormat numberFormat = new DecimalFormat("#.##");
        System.out.println("before test");
		for(int i=0;i<k;i++)
		{   String s =new String();
		s=sm.classify(tweets.get(i).toString());
		//System.out.println("******"+s);
		//	s=tn.performQuery(tweets.get(i).toString());
			//StyleConstants.setForeground(style, Color.red);
	      //  doc.insertString(doc.getLength(),i+1+" "+tweets.get(i)+"\n\n",style);
	      //  doc.insertString(doc.getLength(),i+1+" "+s[i]+"\n\n",style);
			
			if(s.equals("pos"))
			{
				pc++;
				StyleConstants.setForeground(style, Color.red);
		        doc.insertString(doc.getLength(),i+1+" "+tweets.get(i)+"\n\n",style);
		        doc.insertString(doc.getLength(),s+"\n\n",style);
				//positivePercentLabel.setText("positive");
			}
			else
			{
				nc++;
				StyleConstants.setForeground(style, greenColor);
		        doc.insertString(doc.getLength(),i+1+" "+tweets.get(i)+"\n\n",style);
		        doc.insertString(doc.getLength(),s+"\n\n",style);
				//negativePercentLabel.setText("negative");
				
			}
		}
		//JTextPane styles
		TwitterManager.endTime   = System.currentTimeMillis();
		long totalTime = TwitterManager.endTime - TwitterManager.startTime;
		double ac = accuracy(pc,nc);
		System.out.println("Execution time is"+totalTime);
      
			
	//end for
	}//end method

public static double accuracy(int p,int n)
{
		double total= (double)p+(double)n;
	    pp= (p/total)*100;
		np= (n/total)*100;
		System.out.println("positive tweets are"+p);
		System.out.println("negative tweets are"+n);
		System.out.println("total tweets are"+total);
		System.out.println("positive percentage is"+pp);
		System.out.println("negative percentage is"+np);
	
	return 1.0;
	
}
}//end class
