package helpEA;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;

/*
 * TODO I need to have to where I can display multiple error messages from the test cases
 * to the control panel.
 * 
 * I may need to run a small test run of this program and figure out why I am not able 
 * to select platform based on the error that is being printed to windows console.
 */

public class Window_TestAutomationControlPanel implements Runnable
{
	private final static float FONT_SIZE_MULTIPLIER = 1.5f;
	private final static boolean USE_CUSTOM_FRAME_THEME = false;
	
	private Thread[] _threads;
	private TestCase_Result[] _results; 
	
	/* 
	 * This thread is accessed by the java frame GUI thread and also the thread that
	 * this object will be in so I labeled it as volatile so not to run into thread errors.
	 */ 
	private volatile boolean _bEndProgram = false;
	
	private volatile boolean _bThereIsFatalError = false;
	
	private JFrame _frame;
	private boolean _bFrameAlwaysOnTop = true;
	
	private static int screenWidth = GraphicsEnvironment.getLocalGraphicsEnvironment().
			getDefaultScreenDevice().getDisplayMode().getWidth();
	private static int screenHeight = GraphicsEnvironment.getLocalGraphicsEnvironment().
			getDefaultScreenDevice().getDisplayMode().getHeight();
	
	private final static int FRAME_WIDTH = (3 * screenWidth) / 4;
	private final static int FRAME_HEIGHT = (3 * screenHeight) / 4;
	
	private JPanel _contentPane;
	private JTextArea _textArea;
	private JTextArea _textAreaDebug;
	private JScrollPane _scrollPane;
	private JScrollPane _scrollPaneDebug;
	
	
	public Window_TestAutomationControlPanel() 
	{
		_frame = new JFrame("Automation Control Panel");
		_frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		_frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
		_frame.setAlwaysOnTop(_bFrameAlwaysOnTop);
		
		if(USE_CUSTOM_FRAME_THEME) { createFrameTheme(); }
		
		_contentPane = new JPanel();
		_contentPane.setLayout(new BorderLayout());
		
		createAndAddComponentsToContentPane();
		
		_frame.setContentPane(_contentPane);
		_frame.setVisible(true);
	}

	public Window_TestAutomationControlPanel(final Thread[] threads) 
	{
		_threads = threads;
		_results = new TestCase_Result[_threads.length];
		
		_frame = new JFrame("Automation Control Panel");
		_frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		_frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
		_frame.setAlwaysOnTop(_bFrameAlwaysOnTop);
		
		if(USE_CUSTOM_FRAME_THEME) { createFrameTheme(); }
		
		_contentPane = new JPanel();
		_contentPane.setLayout(new BorderLayout());
		
		createAndAddComponentsToContentPane();
		
		_frame.setContentPane(_contentPane);
		_frame.setVisible(true);
	}
	
	@Override
	public void run()
	{
		boolean _bAllThreadsEnded = false;
		
		
		// Stores time when program started.
		long startTime = System.currentTimeMillis();
				
		try
		{
			System.out.println("Control Panel GUI thread has started running.");
			
			// While the user has not wanted to end the program.
			// I am telling the thread to sleep so not to fully load the thread with condition checks.
			// Each iteration of this loop will check the condition statement.
			while(!_bEndProgram) 
			{
				_bAllThreadsEnded = checkIfThreadsDead();
				
				if(_bAllThreadsEnded && _bThereIsFatalError) { _bEndProgram = true; }
				
				Thread.sleep(2000); 
			}
			
			while(!_bAllThreadsEnded)
			{
				_bAllThreadsEnded = checkIfThreadsDead();
				
				// Telling the thread to sleep so not to fully load the thread with checks.
				Thread.sleep(500); 
			}
		}
		catch(Exception e)
		{
			addStringToTestCaseInfoTextArea(e.toString() + "\n", false);
			_bEndProgram = true;
		}
		
		displayRunTime(startTime, System.currentTimeMillis());
		addStringToTestCaseInfoTextArea(" Program has ended!", true);
		
		try { Thread.sleep(5000); } 
		catch (InterruptedException e) { e.printStackTrace(); }
		
		System.exit(0);
	}
	
	/**
	 * Returns value that determined if the user has clicked on the "End Program" button.
	 * @return
	 */
	public synchronized boolean getIsEndProgram() { return _bEndProgram; } 
	
	/**
	 * This will tell the program to end. This is to be used in cases where there is a
	 * fatal error in the program.
	 * @param flag
	 */
	public synchronized void setIsEndProgram(final boolean flag) { _bEndProgram = flag; } 
	
	
	/**
	 * This will tell the program there is a fatal error and that a thread is unable to continue.
	 * @param flag
	 */
	public synchronized void setThereIsFatalError(final boolean flag) { _bThereIsFatalError = flag; }
	
	/**
	 * This will store a reference to the threads that will be used in the automation program.
	 * The only purpose of this is to check to see which threads are still alive in the
	 * program and to read test case results from the threads.
	 * @param threads
	 */
	public void addTestSuiteThreads(final Thread[] threads)
	{
		// Store reference to threads.
		_threads = threads;
		
		// If the _results array has not been created already.
		if(_results == null) 
		{ 
			System.out.println("Test case results array in Control Panel is null!\n" +
					"Creating array...");
			_results = new TestCase_Result[_threads.length];
			for(int i = 0; i < _results.length; ++i) { _results[i] = new TestCase_Result();}
		}
	}
	
	/**
	 * This function will display information about test case on control panel.
	 * 
	 * @param threadNum this is used to determine where to display data on control panel.
	 * @param result this is used for reading data from and displaying it on control panel.
	 */
	public synchronized void printTestCaseInfo(final int threadNum, TestCase_Result result)
	{
		_results[threadNum] = result;
		
		String str = new String(" ==============================================\n" + 
								" CURRENT TEST CASE RUNS\n" +
								" ==============================================\n");
		
		for(int i = 0; i < _results.length; ++i)
		{
			// If the TestCase_Result is not null.
			if(_results[i] != null)
			{
				str += " ==============================================\n";
				str += " Thread # " + _results[i].getThreadNumber() + "\n";
				str += " Test Case # " + _results[i].getCaseNumber() + "\n";
				str += " ==============================================\n";
				str += " Product: " + _results[i].getProduct() + "\n";
				str += " Category: " + _results[i].getCategory() + "\n";
				str += " Issue: " + _results[i].getIssue() + "\n";
				str += " Locale: " + _results[i].getLocale() + "\n";
			}
		}
		
		addStringToTestCaseInfoTextArea(str, false);
		
		List<String> errList = result.getErrorMessages();
		
		// Print out error messages if any.
		if(errList.size() > 0) 
		{ 
			String errorMessages = new String("");
			
			String testCaseInfo = new String(" Product: " + result.getProduct() +
											"\n Category: " + result.getCategory() +
											"\n Issue: " + result.getIssue() +
											"\n Locale: " + result.getLocale() +
											" Errors:\n");
			
			// Add all error messages together in one string.
			for(String err : errList) { errorMessages += err; } 
			addStringToDebugTextArea(testCaseInfo + errorMessages, result.getThreadNumber(), result.getCaseNumber()); 
		}
	}
	
	private boolean checkIfThreadsDead() 
	{
		boolean[] _bThreadsDead = new boolean[_threads.length];
		for(boolean element : _bThreadsDead) { element = false; }
		
		// I have set it true at the beginning of the loop because
		// if there are any threads that are still alive this will be 
		// set to false. If all threads are dead this value will already 
		// be true.
		boolean allThreadsDead = true;
		
		for(int i = 0; i < _bThreadsDead.length; ++i) { _bThreadsDead[i] = !_threads[i].isAlive(); }
		
		for(int i = 0; i < _bThreadsDead.length; ++i)
		{
			// If the thread has not already ended check again to see if it has ended.
			if(!_bThreadsDead[i]) {allThreadsDead = false; }
		}
		
		return allThreadsDead;
	}

	/**
	 * Displays run time on control panel.
	 * @param startTime
	 * @param endTime
	 */
	private void displayRunTime(final long startTime, final long endTime)
	{
		long elapsedTime = endTime - startTime;
		int seconds = (int)(elapsedTime / 1000) % 60;
		int minutes = ((int)(elapsedTime / 1000) / 60) % 60;
		
		// Display elapsed time.
		addStringToTestCaseInfoTextArea(" Run Time: \n Minutes: " + minutes + "\n Seconds: " + seconds, true);
	}

	/**
	 * This function will create and add all the components with their functionality
	 * to a frame for the user to interact with.
	 */
	private void createAndAddComponentsToContentPane()
	{
		// Set frame and task bar icon.
		try { _frame.setIconImage(ImageIO.read(new File("Images/electronic-arts-logo.png"))); } 
		catch (IOException e) { e.printStackTrace(); }
		
		// Create panel to add buttons to.
		JPanel boxLayoutPanel = new JPanel();
		boxLayoutPanel.setLayout(new BoxLayout(boxLayoutPanel, BoxLayout.X_AXIS));
		
		// Create text area.
		_textArea = new JTextArea(30, 40);
		
		if(USE_CUSTOM_FRAME_THEME)
		{
			_textArea.setBackground(Color.BLACK);
			_textArea.setForeground(Color.WHITE);
		}
		
		_textArea.setEditable(false);
		
		// Create text area.
		_textAreaDebug = new JTextArea(30, 40);
		
		if(USE_CUSTOM_FRAME_THEME)
		{
			_textAreaDebug.setBackground(Color.BLACK);
			_textAreaDebug.setForeground(Color.WHITE);
		}
		
		_textAreaDebug.setEditable(false);
		
		// Set font of text area with updated font.
		_textArea.setFont(new Font(Font.SERIF, Font.PLAIN, 
				(int)(_textArea.getFont().getSize() * FONT_SIZE_MULTIPLIER)));
		
		// Set font of text area with updated font.
		_textAreaDebug.setFont(new Font(Font.SERIF, Font.PLAIN,
				(int)(_textAreaDebug.getFont().getSize() * FONT_SIZE_MULTIPLIER)));
		
		// Set text of text area.
		_textAreaDebug.setText( " ==============================================\n" + 
								" TEST CASE ERRORS\n" +
								" ==============================================\n");
		
		// Add text area to scroll pane.
		_scrollPane = new JScrollPane(_textArea);
		_scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		// Add text area to scroll pane.
		_scrollPaneDebug = new JScrollPane(_textAreaDebug);
		_scrollPaneDebug.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
				
		// Create buttons with their functionalities and add them to given panel.
		createButtons(boxLayoutPanel);
		
		// Add all components to _frame.
		_contentPane.add(_scrollPane, BorderLayout.CENTER);
		_contentPane.add(_scrollPaneDebug, BorderLayout.EAST);
		_contentPane.add(boxLayoutPanel, BorderLayout.PAGE_END);
	}
	
	/**
	 * This function creates the custom theme to apply on to the 
	 * frame if you do not wish to use the java/windows default theme.
	 */
	private void createFrameTheme()
	{
		System.out.println("Creating theme...");
		_frame.setUndecorated(true);
		_frame.getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
		
		DefaultMetalTheme metalTheme =
	            new DefaultMetalTheme ()
	            {
	                // Inactive window title color
	                public ColorUIResource getWindowTitleInactiveBackground() 
	                { 
	                    return new ColorUIResource(Color.LIGHT_GRAY); 
	                }

	                // Active window title color
	                public ColorUIResource getWindowTitleBackground() 
	                { 
	                    return new ColorUIResource(Color.WHITE); 
	                }
					
	                // Active window title string foreground color.
					public ColorUIResource getWindowTitleForeground()
					{
						return new ColorUIResource(Color.BLACK);
					}
			 		
			 		// This colors the "metal" bumps on the title bar.
	                public ColorUIResource getPrimaryControlHighlight() 
	                { 
	                    return new ColorUIResource(Color.BLACK); 
	                }
			 		
			 		// This colors the border of the window and also the window
	                // control options icons(minimize, close, etc...).
	                public ColorUIResource getPrimaryControlDarkShadow() 
	                { 
	                    return new ColorUIResource (Color.BLACK); 
	                }

	                // This colors the inside of the window control options
	                // boxes (minimize, close, etc) and also some of the components.
	                // As of right now it only colors the foreground of the 
	                // scroll bar.
	                public ColorUIResource getPrimaryControl() 
	                { 
	                    return new ColorUIResource(Color.WHITE); 
	                }

	                // This colors the borders around the Layouts and it also colors
	                // the window operations(minimize, close, etc...).
	                public ColorUIResource getControlHighlight ()
	                {
	                    return new ColorUIResource(Color.DARK_GRAY); 
	                }
	                
	                // This colors the borders around the Layouts when
	                // frame is inactive.
	                public ColorUIResource getControlDarkShadow ()
	                {
	                    return new ColorUIResource(Color.LIGHT_GRAY); 
	                }
	                
	                // This colors the inside of the layouts that have blank spaces.
	                public ColorUIResource getControl ()
	                {
	                    return new ColorUIResource (Color.WHITE); 
	                }
	                
	                public FontUIResource getWindowTitleFont()
	                {
	                	return new FontUIResource(new Font(Font.SERIF, Font.BOLD, screenWidth/100));
	                }
	            };
		            
        MetalLookAndFeel.setCurrentTheme(metalTheme);

        try
        {
            UIManager.setLookAndFeel(new MetalLookAndFeel());
        }
        catch (Exception e)
        {
            e.printStackTrace ();
        }   

        SwingUtilities.updateComponentTreeUI (_frame);
	}
	
	/**
	 * This function will create buttons with their own characteristics and functionality. 
	 * @param panel is where the buttons will be added to.
	 */
	private void createButtons(final JPanel panel)
	{
		Dimension maxButtonSize = new Dimension(screenWidth/10, screenHeight/10);
		Dimension preferredButtonSize = new Dimension(screenWidth/10, screenHeight/17);
		
		//=======================================================================================//
		// Create quit program button.
		JButton quitProgBtn = new JButton("Quit Program");
		quitProgBtn.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				if(!_bEndProgram)
				{
					_bEndProgram = true;
					quitProgBtn.setBackground(Color.LIGHT_GRAY);
					quitProgBtn.setEnabled(false);
					//addStringToTextArea("Pressed: " + quitProgBtn.getText());
				}
			}
		});
		
		if(USE_CUSTOM_FRAME_THEME) { quitProgBtn.setBackground(new Color(60, 217, 255)); }
		quitProgBtn.setPreferredSize(preferredButtonSize);
		quitProgBtn.setMaximumSize(maxButtonSize);
		//=======================================================================================//
		
		JButton _toggleFrameAlwaysTopBtn = new JButton("Window Always On Top\n");
		_toggleFrameAlwaysTopBtn.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						// Toggle frame always on top by sending opposite of current value.
						_bFrameAlwaysOnTop = !_bFrameAlwaysOnTop;
						_frame.setAlwaysOnTop(_bFrameAlwaysOnTop);
						_toggleFrameAlwaysTopBtn.setBackground((_bFrameAlwaysOnTop ? Color.GREEN : Color.RED));
						
						//addStringToTextArea("Pressed: " + _toggleFrameAlwaysTopBtn.getText());
					}
				});
		
		_toggleFrameAlwaysTopBtn.setMaximumSize(maxButtonSize);
		_toggleFrameAlwaysTopBtn.setBackground((_bFrameAlwaysOnTop ? Color.GREEN : Color.RED));
		//=======================================================================================//
		
		// Add all buttons to panel.
		panel.add(quitProgBtn);
		panel.add(_toggleFrameAlwaysTopBtn);
	}
	
	/**
	 * This is used to add a string to the JTextArea component located in the control panel.
	 * @param text is a string of text that is to be displayed on control panel.
	 * @param keepPrevText is a flag that determines if you want to keep all previous text that
	 * 	is already displayed on the text area of the control panel.
	 */
	private synchronized void addStringToTestCaseInfoTextArea(final String text, final boolean keepPrevText)
	{
		
		if(keepPrevText) { _textArea.setText(_textArea.getText() + text + "\n"); }
		else { _textArea.setText(text + "\n"); }
		
		_textArea.update(_textArea.getGraphics());
	}
	
	private synchronized void addStringToDebugTextArea(final String text, final int threadNum, 
			final int caseNum)
	{
		String outputString = new String("");
		outputString += " ==============================================\n";
		outputString += " Thread # " + threadNum + "\n";
		outputString += " Test Case # " + caseNum + "\n";
		outputString += " ==============================================\n";
		_textAreaDebug.setText(_textAreaDebug.getText() + outputString + text + "\n");
		_textAreaDebug.update(_textAreaDebug.getGraphics());
	}
	
	public static void main(String[] args) throws InterruptedException 
	{
		Window_TestAutomationControlPanel obj = new Window_TestAutomationControlPanel();
		
		Thread thread = new Thread(obj);
		thread.run();
	}
	
}
