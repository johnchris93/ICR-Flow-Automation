/**
 * @author jsandoval@contractor.ea.com
 * Date: 11/30/2016
 *
 * All Windows excel files should be read in as .xlsx files since no other format
 * is supported by apache poi excel file reader. If not the correct format, it will
 * throw INVALIDFORMATEXCEPTION. 
 * 
 * There are times where the given excel spreadsheet may be formatted oddly(ex. missing row numbers)
 * and you may need to do some file conversions to get rid of that format.
 * 
 * Steps for conversion inside Microsoft Excel:
 * #1 Go to file Tab.
 * #2 Go to export
 * #3 Click "Change File Type"
 * #4 Click "Other File Types"
 * #5 Click CSV File type.
 * #6 Once Converted go through Steps 1-4 again.
 * #7 Click xlsx file type
 * 
 * Once those steps have been completed weird formating should be gone.
 */

package helpEA;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelFile_Read_TestCases 
{
	private String _pathToFile;
	private String _sheetName;
	
	private XSSFWorkbook _excelWorkBook;
	private XSSFSheet _excelSheet;
	
	private List<List<String[]>> _testCases_multBrowser;
	private int[] _browserTestSuiteStartRow;
	
	private int _numBrowsers;

	private static int START_ROW = 0;
	private int END_ROW = 0;
	private int NUM_CASES = 0;
	private static int NUM_STRING_VAL = 7;
	
	// Used to access the strings located inside the List<String[]> container.
	public static final int GAME_TITLE = 0;
	public static final int CATEGORY = 1;
	public static final int ISSUE = 2;
	
	/** The value read in is the locale code.*/
	public static final int LOCALE = 3;
	
	/**
	 * Read in an excel file from the hard disk into a class object.
	 * @param fileName A sting value that contains the file name with .xlsx appended to the end of the string.
	 * @param sheetName Name of the sheet you want to access in the excel file on the hard disk.
	 * @param numBrowsers Number of browsers you plan to run the test cases in.
	 * @throws FileNotFoundException if the excel file name string is malformed or the excel file 
	 * 		does not already exist on the hard disk.
	 * @throws IOException
	 */
	public ExcelFile_Read_TestCases(final String fileName, final String sheetName, final int numBrowsers) 
			throws FileNotFoundException, IOException 
	{
		int numCasesPerBrowser = 0;
		int remainder = 0;
		String copiedFileName = fileName;
		
		// If file name is not formatted correctly.
		if(!copiedFileName.contains(".xlsx")) { copiedFileName += ".xlsx"; }
		
		this._pathToFile = TestAutomation_Runner.PATH_TO_EXCEL_FILE;
		this._sheetName = sheetName;
		
		_excelWorkBook = new XSSFWorkbook(new FileInputStream(_pathToFile));
		if(_excelWorkBook == null) {System.out.println("workbook is null.");}
		//System.out.println("Created excel workbook...");
		
		_excelSheet = _excelWorkBook.getSheet(_sheetName);
		if(_excelSheet == null) {System.out.println("sheet is null.");} 
		//System.out.println("Created excel sheet...");
		
		// Store number of browsers in object.
		this._numBrowsers = numBrowsers;
		
		// Get start and end row of test case excel sheet.
		getStartAndEndRowIndex();
		System.out.println("Calculated first and last row of excel sheet...");
		
		// Calculate the number of cases.
		NUM_CASES = (END_ROW + 1) - START_ROW;
		
		// Perform integer division.
		numCasesPerBrowser = NUM_CASES / _numBrowsers;
		remainder = NUM_CASES % _numBrowsers;
		
		createMultBrowserTestCases(numCasesPerBrowser, remainder);
	}
	
	/**
	 * @param testCaseNum // Value should not be less than 1.
	 * @param result // Result object that is returned from test case run of EA Help.
	 */
	public synchronized void writeTestCaseResultToBuffer(final int excelStartRow, 
			final int testCaseNum, final TestCase_Result result)
	{
		setEmailColumnValue(excelStartRow, testCaseNum, result.getEmailPosition());
		setChatColumnValue(excelStartRow, testCaseNum, result.getChatPostion());
		setPhoneColumnValue(excelStartRow, testCaseNum, result.getPhonePosition());
		setAHQColumnValue(excelStartRow, testCaseNum, result.getIsAHQVisible());
		setErrorColumnValue(excelStartRow, testCaseNum, result.getErrorMessages());
	}
	
	/**
	 * Writes out all the data added to the cells within the program
	 * out to the excel sheet on the hard disk.
	 */
	public synchronized void writeBufferedDataToExcelSheet()
	{
		// Write data to excel file.
		try 
		{
			FileOutputStream fileOut = new FileOutputStream(_pathToFile);
			_excelWorkBook.write(fileOut);
			fileOut.flush(); // Flushes this output stream and forces any buffered output bytes to be written out.
			fileOut.close(); // Close and release file stream.
		} 
		catch (Exception e) 
		{
			System.out.println("An error occured when writing to excel file: ");
			e.printStackTrace();
		}
	}
	
	/**
	 * This function will create a List(String[]) for each browser.
	 * @param numCasesPerBrowser
	 * @param remainder
	 */
	private void createMultBrowserTestCases(final int numCasesPerBrowser, final int remainder)
	{	
		// This dictates the first row where the browser will start testing. 
		int currentBrowserTestRow = START_ROW; 
		
		// Parallel array that determines how many test cases per browser.
		int[] arr_numCasesPerBrowser = new int[_numBrowsers];
		
		// Create test case list for each browser.
		_testCases_multBrowser = new ArrayList<List<String[]>>(0);
		
		// Create array that will hold each browser excel start row.
		_browserTestSuiteStartRow = new int[_numBrowsers];
		
		// Initialize all elements of array to 0 to prevent all elements 
		// being filled with garbage data from compiler.
		for(int i = 0; i < _numBrowsers; ++i) { arr_numCasesPerBrowser[i] = 0; }
		
		// Distribute the remainder of cases among browsers.
		for(int i = 0; i < remainder; ++i) { arr_numCasesPerBrowser[i] += 1; }
		
		// Set array of integers that will hold how many test cases per browser.
		for(int i = 0; i < arr_numCasesPerBrowser.length; ++i)
		{
			arr_numCasesPerBrowser[i] += numCasesPerBrowser;
		}
		
		// Create test case list for each browser and create String[] for each test case in list.
		for(int i = 0; i < _numBrowsers; ++i)
		{
			// Create new List<String[]> per browser with an initial 0 elements.
			_testCases_multBrowser.add(new ArrayList<String[]>(0));
			
			// Create new string arrays for list size.
			for(int ind = 0; ind < arr_numCasesPerBrowser[i]; ++ind)
			{
				// Create String[] for each element in List<String[]>
				_testCases_multBrowser.get(i).add(ind, new String[NUM_STRING_VAL]);
			}
		}
		
		// Read in test case values for each browser.
		for(int i = 0; i < _numBrowsers; ++i)
		{
			List<String[]> curBrowserTestCases = _testCases_multBrowser.get(i);
			
			_browserTestSuiteStartRow[i] = currentBrowserTestRow;
			
			//System.out.println("Start row for browser # " + i + ": " + (currentBrowserTestRow + 1));
			
			//System.out.println("Num Cases for browser # " + (i + 1) + ": " + curBrowserTestCases.size());
			
			// For every element in List<String[]>
			for(int j = 0; j < _testCases_multBrowser.get(i).size(); ++j)
			{
				curBrowserTestCases.get(j)[GAME_TITLE] = _excelSheet.getRow(currentBrowserTestRow).
						getCell(Data_Warehouse.EXCEL_PRODUCT_COL).getStringCellValue();
				curBrowserTestCases.get(j)[CATEGORY] = _excelSheet.getRow(currentBrowserTestRow).
						getCell(Data_Warehouse.EXCEL_CATEGORY_COL).getStringCellValue();
				curBrowserTestCases.get(j)[ISSUE] = _excelSheet.getRow(currentBrowserTestRow).
						getCell(Data_Warehouse.EXCEL_ISSUE_COL).getStringCellValue();
				curBrowserTestCases.get(j)[LOCALE] = _excelSheet.getRow(currentBrowserTestRow).
						getCell(Data_Warehouse.EXCEL_LOCALE_COL).getStringCellValue();
				
				if(TestAutomation_Runner.DELETE_VALUES_IN_ERROR_COL)
				{
					_excelSheet.getRow(currentBrowserTestRow).getCell(
							Data_Warehouse.EXCEL_ERROR_COL).setCellValue(" ");
					_excelSheet.getRow(currentBrowserTestRow).setHeightInPoints(
							_excelSheet.getDefaultRowHeightInPoints());
				}
				
				// Increment index.
				++currentBrowserTestRow;
			}
		}
		
		// Write the deleted error column cells to excel file on hard disk.
		if(TestAutomation_Runner.DELETE_VALUES_IN_ERROR_COL) { writeBufferedDataToExcelSheet(); }
	}
	
	/**
	 * Looks through Product column for the string values START_ROW and END_ROW.
	 * These two values will determine which test cases will be read in to program.
	 */
	private void getStartAndEndRowIndex()
	{
		String currentTitle = "test";
		int index = 0;
		
		// Find where START_ROW is located in excel file.
		while(!currentTitle.equals("START_ROW"))
		{
			try
			{
				currentTitle = _excelSheet.getRow(index).getCell(Data_Warehouse.EXCEL_PRODUCT_COL).getStringCellValue();
				
				if(currentTitle.equals("START_ROW"))
				{
					START_ROW = index + 1; // Get the current row index.
					//System.out.println("START_ROW: " + START_ROW);
					break; // Exit loop.
				}
			}
			catch(Exception e) // Catches row doesn't exist exception.
			{
				//e.printStackTrace();
				++index; // Increase index.
				continue; // Immediately end this iteration of loop.
			}
			
			++index; // Increase index.
		}
		
		// Calculate where END_ROW is located in excel file.
		while(!currentTitle.equals("END_ROW"))
		{
			try
			{
				currentTitle = _excelSheet.getRow(index).getCell(Data_Warehouse.EXCEL_PRODUCT_COL).getStringCellValue();
				
				if(currentTitle.equals("END_ROW"))
				{
					END_ROW = index - 1; // Get the current row index.
					//System.out.println("END_ROW: " + END_ROW);
					break; // Exit loop.
				}
			}
			catch(Exception e) // Catches row doesn't exist exception.
			{
				//e.printStackTrace();
				++index; // Increase index.
				continue; // Immediately end this iteration of loop.
			}
			
			++index; // Increase index.
		}
	}

	/**
	 * Returns a List that will contain List(String[]) elements that will
	 * determine how many test cases each thread will run.
	 */
	public List<List<String[]>> getTestCases() { return _testCases_multBrowser; }
	
	/**
	 * Returns an array of int's that is parallel to the List that is returned 
	 * by the getTestCases() that dictates where the start row for each thread
	 * in the excel sheet. This is mostly for writing errors out to the excel sheet.
	 */
	public int[] getTestSuiteBrowserStartRows() { return _browserTestSuiteStartRow; } 
	
	/**
	 * @param testCaseNum // Value should not be less than 1.
	 * @param position // Specifying the position that the option is displayed(1, 2, 3).
	 */
	public void setEmailColumnValue(final int excelStartRow, final int testCaseNum, final int position)
	{
		XSSFRow row = _excelSheet.getRow((testCaseNum - 1) + excelStartRow);
		if(position > 0) { row.getCell(Data_Warehouse.EXCEL_EMAIL_COL).setCellValue(position); }
		else { row.getCell(Data_Warehouse.EXCEL_EMAIL_COL).setCellValue("OFF"); }
	}
	
	/**
	 * @param testCaseNum // Value should not be less than 1.
	 * @param position // Specifying the position that the option is displayed(1, 2, 3).
	 */
	public void setChatColumnValue(final int excelStartRow, final int testCaseNum, final int position)
	{
		XSSFRow row = _excelSheet.getRow((testCaseNum - 1) + excelStartRow);
		if(position > 0) { row.getCell(Data_Warehouse.EXCEL_CHAT_COL).setCellValue(position); }
		else { row.getCell(Data_Warehouse.EXCEL_CHAT_COL).setCellValue("OFF"); }
	}
	
	/**
	 * @param testCaseNum // Value should not be less than 1.
	 * @param position // Specifying the position that the option is displayed(1, 2, 3).
	 */
	public void setPhoneColumnValue(final int excelStartRow, final int testCaseNum, final int position)
	{
		XSSFRow row = _excelSheet.getRow((testCaseNum - 1) + excelStartRow);
		if(position > 0) { row.getCell(Data_Warehouse.EXCEL_PHONE_COL).setCellValue(position); }
		else { row.getCell(Data_Warehouse.EXCEL_PHONE_COL).setCellValue("OFF"); }
	}
	
	/**
	 * @param testCaseNum // Value should not be less than 1.
	 * @param error // Error string to be written to excel sheet.
	 */
	public void setErrorColumnValue(final int excelStartRow, final int testCaseNum, 
			final List<String> errors)
	{
		XSSFRow row = _excelSheet.getRow((testCaseNum - 1) + excelStartRow);
		
		// Set height of row based on how many errors there are.
		// If there are no errors I will still size column to at least 1 error so the whole
		// row doesn't collapse.
		row.setHeightInPoints((errors.size() == 0 ? 1 : errors.size() + 1) * _excelSheet.getDefaultRowHeightInPoints());
				
		String appendCellValue = new String("");
		for(String errMsg : errors) { appendCellValue += errMsg; } 
		
		//System.out.println("Writing to Cell Number: " + (testCaseNum + excelStartRow));
		//System.out.println("Message to be written:\n" + appendCellValue);
		row.getCell(Data_Warehouse.EXCEL_ERROR_COL).setCellValue(appendCellValue);
	}
	
	/**
	 * @param isDisplayed // Boolean flagging if is displayed or not.
	 * @param testCaseNum // Value should not be less than 1.
	 */
	public void setAHQColumnValue(final int excelStartRow, final int testCaseNum, 
			final boolean isDisplayed)
	{
		XSSFRow row = _excelSheet.getRow((testCaseNum - 1) + excelStartRow);
		row.getCell(Data_Warehouse.EXCEL_AHQ_COL).setCellValue(isDisplayed ? "YES" : "OFF");
	}
}
