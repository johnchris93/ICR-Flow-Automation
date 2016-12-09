package helpEA;

import java.util.Date;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/** 
 *  Help EA web-site automation class that is designed to be run in a thread and have concurrent 
 *  threads running test cases. If you do not wish to use threads, the run() method should 
 *  be able to run all the test cases that was loaded into the constructor without having to 
 *  load a class object into a thread.
 **/
public class TestCase_Run implements Runnable
{
	private TestCase_Browser _browser;
	private WebDriver _driver;
	private EA_Help_Automation_Helper _helpEAAutomation;
	private String _qaWebAddress;
	private int _threadNum;
	private int _testCaseNum;
	private int _excelStartRow;
	
	private static boolean _hasRanSetup = false;
	
	private Window_TestAutomationControlPanel _controlPanel;
	
	private List<String[]> _testCases;
	
	private ExcelFile_Read_TestCases _excelFile;
	
	private String _curLocale = new String(" ");
	private String _curCategory = new String(" ");
	
	private boolean _fillVOGForm = false;
	
	// TODO I need to create a check in both selectCategory() and selectIssue() that checks
	// if there are any discrepancies in localization such as missing symbols, not translating,
	// at all, and just straight question marks.
	
	public TestCase_Run(final String webAddress, final ExcelFile_Read_TestCases excelFile,
			TestCase_Browser browser, List<String[]> testCases, final int threadNumber,
			final int excelStartRow, boolean fillVOGForm) throws Exception
	{	
		// Store reference to browser.
		_browser = browser;
		
		// Store reference to browser.
		_driver = _browser.getWebDriver();
		
		// Create automation class.
		_helpEAAutomation = new EA_Help_Automation_Helper(this);
		
		// Store reference to web address that accesses test environment.
		_qaWebAddress = webAddress;
		
		// Store reference to test cases.
		_testCases = testCases;
		
		// Store reference to excel file.
		_excelFile = excelFile;
		
		// Store reference to thread number.
		_threadNum = threadNumber;
		
		// Store value for start row in excel file.
		_excelStartRow = excelStartRow;
		
		// Flag to fill out VOG form.
		_fillVOGForm = fillVOGForm;
	}
	
	public TestCase_Run(final Window_TestAutomationControlPanel controlPanel, 
			final String webAddress, final ExcelFile_Read_TestCases excelFile,
			TestCase_Browser browser, List<String[]> testCases, final int threadNumber,
			final int excelStartRow, boolean fillVOGForm) throws Exception
	{
		// Load necessary values for test run.
		if(!_hasRanSetup)
		{
			_hasRanSetup = true;
		}
		
		_controlPanel = controlPanel;
		
		// Store reference to browser.
		_browser = browser;
		
		// Store reference to browser.
		_driver = _browser.getWebDriver();
		
		// Create automation class.
		_helpEAAutomation = new EA_Help_Automation_Helper(this);
		
		// Store reference to web address that accesses test environment.
		_qaWebAddress = webAddress;
		
		// Store reference to test cases.
		_testCases = testCases;
		
		// Store reference to excel file.
		_excelFile = excelFile;
		
		// Store reference to thread number.
		_threadNum = threadNumber;
		
		// Store value for start row in excel file.
		_excelStartRow = excelStartRow;
		
		// Flag to fill out VOG form.
		_fillVOGForm = fillVOGForm;
	}
	
	/**Returns current locale that the thread is currently on.*/
	public String getCurrentLocale() { return _curLocale; }
	
	/**Returns current category that the thread is currently on.*/
	public String getCurrentCategory() { return _curCategory; }
	
	public TestCase_Browser getBrowser() { return _browser; }
	
	/**This function begins the thread run.**/
	@Override
	public void run()
	{
		System.out.println("Thread: " + _threadNum + " has started running.");
		
		try
		{
			// Run all test cases.
			for(int i = 0; i < _testCases.size(); ++i)
			{
				// Get current time.
				long startCaseTime = System.currentTimeMillis();
				
				_testCaseNum = i + 1;
				_curLocale = _testCases.get(i)[ExcelFile_Read_TestCases.LOCALE];
				_curCategory = _testCases.get(i)[ExcelFile_Read_TestCases.CATEGORY];
				String curGameTitle = _testCases.get(i)[ExcelFile_Read_TestCases.GAME_TITLE];
				String curIssue = _testCases.get(i)[ExcelFile_Read_TestCases.ISSUE];
				
				// Store information about case in test result.
				_browser.getTestCaseResult().setThreadNumber(_threadNum);
				_browser.getTestCaseResult().setCaseNumber(_testCaseNum);
				_browser.getTestCaseResult().setProduct(curGameTitle);
				_browser.getTestCaseResult().setCategory(_curCategory);
				_browser.getTestCaseResult().setIssue(curIssue);
				
				// I grabbed the country name rather than the Help EA web-site HTML value
				// so when printing out value is says country name rather than HTML value.
				_browser.getTestCaseResult().setLocale(Data_Warehouse.MAP_LOCALE_CODES.get(_curLocale));
				
				// Print info to control panel.
				_controlPanel.printTestCaseInfo(_threadNum, _browser.getTestCaseResult());
				
				try
				{
					runTestCase(_qaWebAddress, _browser, _testCaseNum, curGameTitle,
							_curCategory, curIssue, _curLocale);
					
					// Write results to excel file buffer.
					_excelFile.writeTestCaseResultToBuffer(_excelStartRow, _testCaseNum, 
							_browser.getTestCaseResult());
				}
				catch(Exception e) // If case throws exception, write error to excel file and continue loop.
				{
					e.printStackTrace();
					_browser.getTestCaseResult().setErrorMessage(e.getMessage());
					_excelFile.writeTestCaseResultToBuffer(_excelStartRow, _testCaseNum, 
							_browser.getTestCaseResult());
				}
				
				// Print info to control panel.
				_controlPanel.printTestCaseInfo(_threadNum, _browser.getTestCaseResult());
				
				// Reset test case values so not to carry data from current result to
				// next test case results by accident.
				_browser.getTestCaseResult().resetValues();
				
				// Pass start time and end time to display run time.
				displayRunTime(startCaseTime, System.currentTimeMillis());
				
				// Write all data received from test case to excel sheet.
				_excelFile.writeBufferedDataToExcelSheet();
				
				// Reset class values.
				_helpEAAutomation.resetValues();
				
				// If the program is flagged is to end.
				if(_controlPanel.getIsEndProgram()) { break; /* Break out of loop.*/ }
			}
		}
		catch(Exception e)
		{
			e.printStackTrace(); // Print exception stack.
			System.out.println("Time of crash: " + (new Date()).toString()); // Print current time.
			_controlPanel.setThereIsFatalError(true);
			_browser.getWebDriver().quit(); // Close and clean up browser.
			return; // Jump out of function.
		}
		
		// Close and clean up browser.
		_browser.getWebDriver().quit();
	}
	
	private static void displayRunTime(final long startTime, final long endTime)
	{
		long elapsedTime = endTime - startTime;
		int seconds = (int)(elapsedTime / 1000) % 60;
		int minutes = ((int)(elapsedTime / 1000) / 60) % 60;
		
		// Display elapsed time.
		System.out.println("Run Time: \nMinutes: " + minutes + "\nSeconds: " + seconds);
	}

	private static void selectLocale(final WebDriver _driver, final String locale) 
			throws InterruptedException
	{
		long waitedTime = 0;
		int timeToSleep = 2000;
		
		// Click on locale drop down menu.
		_driver.findElement(By.cssSelector("div#utility-bar-header > ul.nav.nav-pills > "
				+ "li#country-selector")).click();
		Thread.sleep(timeToSleep);
		waitedTime += timeToSleep;
		
		// Get all locale web elements
		List<WebElement> localeOptions = _driver.findElements(
				By.cssSelector("ul.dropdown-menu.affix-top > li"));
		
		for(WebElement el : localeOptions)
		{
			// Search is looking for a string that has the same given value in it's data model.
			if(el.getAttribute("data-model").contains(locale))
			{
				_driver.findElement(By.id(el.getAttribute("id"))).click();
				break;
			}
		}
		
		Thread.sleep(timeToSleep);
		waitedTime += timeToSleep;
	}
	
	/**
	 * This function will run all the steps for each test case.
	 * */
	private void runTestCase(final String webAddress, TestCase_Browser browser, 
			final int testCaseNum, final String gameTitle, final String topic, 
			final String issue, final String locale) 
			throws Exception
	{
		long waitedTime = 0;
		int timeToSleep = 2000;
		
		TestCase_Result testResult = _browser.getTestCaseResult();
		
		// Navigate to given web address.
		_driver.get(webAddress);
		Thread.sleep(timeToSleep);
		waitedTime += timeToSleep;
		
		// Set locale.
		selectLocale(_driver, locale);
		
		// Click on contact us button at top of web-page.
		_driver.findElement(By.cssSelector("ul.nav.nav-pills > li#contactus > "
				+ "a.contact_us > span")).click();
		Thread.sleep(timeToSleep);
		waitedTime += timeToSleep;
		
		/* Try and check if a 404 page is shown. */
		try
		{
			if(_driver.findElement(By.cssSelector("div.container.app.page.error-404")).isDisplayed())
			{
				System.out.println("TEST CASE FAILED: Page Error 404");
				throw new Exception("TEST CASE FAILED: Page Error 404 after clicking \"Contact Us \""
						+ "on EA help home page.");
			}
		} 
		catch(Exception e) {/*If no 404 page is shown continue with the test case.*/}
		
		// Select game.
		try{ _helpEAAutomation.selectGame(_driver, gameTitle, browser.getIsLoggedIn()); }
		catch(Exception e) { e.printStackTrace(); throw new Exception(e.getMessage()); }
		
		// Select platform 
		if(!gameTitle.equals("Pogo"))
		{
			try { _helpEAAutomation.selectPlatform(_driver); }
			catch(Exception e) { e.printStackTrace(); throw new Exception(e.getMessage()); }
		}
		
		// Select topic.
		try { _helpEAAutomation.selectCategory(_driver, topic); }
		catch(Exception e) { e.printStackTrace(); throw new Exception(e.getMessage()); }
		
		// Select issue.
		try { _helpEAAutomation.selectIssue(_driver, issue); }
		catch(Exception e) { e.printStackTrace(); throw new Exception(e.getMessage()); }
		
		// Wait additional time to make sure everything is loaded.
		Thread.sleep(timeToSleep);
		waitedTime += timeToSleep;
		
		// Check to see if there are any additional options that need to be selected
		// based on the current game title.
		try { _helpEAAutomation.selectGameSpecificOptions(_driver, gameTitle); }
		catch(Exception e) { e.printStackTrace(); Thread.sleep(1000);}
		
		// Fill out VOG
		if(_fillVOGForm)
		{
			try { _helpEAAutomation.fillVOG(_driver); }
			catch(Exception e){ e.printStackTrace(); throw new Exception("Cannot complete VOG form: \n" + e.getMessage()); }
		}
		
		// Click "Next" button after selection options in the old ICR.
		if(_helpEAAutomation.getIsOldICR())
		{
			// Click "Next" button.
			_driver.findElement(By.cssSelector("a.btn-next")).click();
			Thread.sleep(3000);
		}
		else
		{
			// Click "Contact Us" after selecting options.
			try
			{
				_driver.findElement(By.cssSelector("div.icr-submit-container > a#requireContact >"
						+ "div > span")).click();
				Thread.sleep(4000);
				waitedTime += 4000;
			}
			catch(Exception e) // WebDriver will try for about 4-6 seconds to click on element before throwing exception.
			{
				System.out.println("TEST CASE FAILED: The page hanged and not able to click \"Contact Us\"");
				
				e.printStackTrace();
				
				throw new Exception("TEST CASE FAILED: The page hanged after selecting case issue "
						+ "and not able to click \"Contact Us\" \n" + e.getMessage());
			}
		}
			
		// If not logged in.
		if(!browser.getIsLoggedIn())
		{
			browser.setIsLoggedIn(true);
			_helpEAAutomation.logIn(_driver, browser.getLoginEmail(), browser.getLoginPassword());
		}
		Thread.sleep(1000);
		waitedTime += 1000;
		
		_helpEAAutomation.checkForContactOptions(_driver, testResult);
		_helpEAAutomation.checkForAHQOption(_driver,  testResult);
		
		Thread.sleep(1000);
		waitedTime += 1000;
		
		// Create ticket if flag is true.
		if(TestAutomation_Runner.FLAG_CREATE_CHAT_TICKET)
		{
			try{
				if(testResult.getIsChatVisible()) { _helpEAAutomation.createChatTicket(_driver); }
			}
			catch(Exception e) { throw new Exception(e.getMessage()); }
		}
		
		//QA1_Help_EA_Automation.checkForAfterHourOption(_driver, afterHourMessage);
	}
}