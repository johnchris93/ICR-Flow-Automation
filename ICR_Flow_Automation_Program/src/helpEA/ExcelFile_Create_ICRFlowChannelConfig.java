/**
 * @author jsandoval@contractor.ea.com
 * Date: 12/1/2016
 * 
 * This class will allow you create an excel file on the hard disk. This class will only
 * fill in data pertaining to the EA Help ICR Flow.
 * 
 * You are able to configure which locales will be written to excel sheet
 *     * Note: List of supported locales below.
 *     
 * You are able to configure which games will be written to excel sheet.
 *     * Note: The game titles are case sensitive and need to be
 *          spelled exactly as it is spelled on the EA 
 *          Help web-site.
 *          
 * You are able to configure which categories will be written to excel sheet.
 *     * Note: All issues (as to my knowledge) will be included for each
 *         category.
 * 
 * The values that are written to excel sheet will be fed into automation program.
 * 
 * List of Supported Locales (Copy locale as is in locale list if you plan to add to array):
 * United States
 * Australia
 * Brazil
 * Canada
 * Canada(French)
 * Czech Republic
 * Denmark
 * Germany
 * Spain
 * Finland
 * France
 * China
 * India
 * Italy
 * Japan
 * Korea
 * Hungary
 * Mexic0
 * Netherlands
 * New Zealand
 * Norway
 * Poland
 * Portugal
 * Russia
 * Switzerland(German)
 * Singapore
 * South Africa
 * Suisse - French
 * Sweden
 * Svizzera - Italiano
 * Taiwan
 * Turkey
 * United Kingdom
 */

package helpEA;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Creates excel file on hard disk with given file name, sheet name, and at the given path.
 * @author jsandoval
 *
 */
public class ExcelFile_Create_ICRFlowChannelConfig {
	
	private static String _excelFileName = TestAutomation_Runner.EXCEL_FILE_NAME;
	private static String _excelSheetName = TestAutomation_Runner.EXCEL_SHEET_NAME;
	private static String _fileLocation = TestAutomation_Runner.EXCEL_FILE_FOLDER_PATH;
	private static String _pathToFile = _fileLocation + _excelFileName;
	
	/*
	 * Be sure to encapsulate each product and locale in quotations and separate them by commas
	 * such as: 
	 * _products = {"FIFA 17", "Star Wars Battlefront", "Star Wars: Galaxy of Heroes"}; 
	 * _locales = {"United Kingdom", "South Africa", "Poland"};
	 */
	private final static String[] _products = {"Pogo", "FIFA 17", "Madden 16"};
	
	private final static String[] _locales = {"United States", "Russia", "France"};
	
	/* Flag used to determine which categories to include in excel file create */
	private final static boolean ADD_GAME_INFO = true;
	private final static boolean ADD_CODES_AND_PROMO = false;
	private final static boolean ADD_MANAGE_MY_ACCNT = false;
	private final static boolean ADD_MISSING_CONTENT = false;
	private final static boolean ADD_ORDERS = false;
	private final static boolean ADD_REPORT_BUG = false;
	private final static boolean ADD_REPORT_HARASSMENT = false;
	private final static boolean ADD_TECHNICAL_SUPPORT = true;
	private final static boolean ADD_WARRANTY = false;
	
	// Holds all the issues that correlate to a particular category.
	private final static Map<String, String[]> LIST_CATEGORY_ISSUES = 
								new HashMap<String, String[]>(0);
	
	// Used to hold the strings for the categories that will be written to excel sheet.
	private static String[] _activeCategories;
	
	private static XSSFWorkbook _excelWorkbook;
	private static XSSFSheet _excelSheet;
	
	/**
	 * This code will only execute if it is ran as a stand-alone program.
	 * 
	 * The main() function will wipe and over-write the previous excel file listed
	 * in the excel file name at the give path. So if you have a file named test_1.xlsx
	 * and you run the program again and do not change the excel file name the program will
	 * wipe all data in excel file and over-write it.
	 */
	public static void main(String[] args) 
	{
		Data_Warehouse.load();
		
		// Create excel workbook.
		try 
		{ 
			// Scanner to read input from console (this is used because I plan to run this program 
			// from IDE and command line).
			Scanner scanner = new Scanner(System.in);
			
			// Holds user response typed into the console.
			String userResponse = new String();
			
			// Obtain reference to excel file on local drive.
			_excelWorkbook = new XSSFWorkbook(new FileInputStream(_pathToFile)); 
			
			// Print out warning message if excel file already exists.
			System.out.print("WARNING:\nExcel already exists: " + _excelFileName + 
					"\nWould you like to continue program (yes/no)?: ");
			
			// Read user response from input scanner.
			userResponse = scanner.next();
			
			// Convert all characters in string to lower case.
			userResponse.toLowerCase();
			
			switch(userResponse)
			{
				case "no":
				case "n":
					return; // Jump out of function.
				default: // If any other answer besides no.
					throw new Exception("continuing program");
			}
		} 
		catch (Exception e) 
		{
			System.out.println("Excel file does not exist at path: " + _pathToFile);
			System.out.println("Creating excel file: " + _excelFileName);
			
			_excelWorkbook = new XSSFWorkbook();
			_excelSheet = _excelWorkbook.createSheet(_excelSheetName);
			
			// Write newly created excel file to hard disk.
			try 
			{
				FileOutputStream fileOut = new FileOutputStream(_pathToFile);
				_excelWorkbook.write(fileOut);
				fileOut.flush();
				fileOut.close();
			} 
			catch (Exception e2) 
			{
				e2.printStackTrace();
				return;
			}
			
			System.out.println("Succesfully created excel file: " + _excelFileName + 
					"\nCreated at: " + _pathToFile);
		}
		
		// Get sheet from workbook based on name of sheet.
		_excelSheet = _excelWorkbook.getSheet(_excelSheetName);
		
		loadDataContainerValues();
		//clearExcelSheet();
		createHeaderColumns();		
		fillSheetWithData();
		
		//Write initial data to file.
		writeBufferedDataToExcelSheet();
		
		// Format the excel sheet.
		formatExcelSheet();
		
		//Write formated cells to file.
		writeBufferedDataToExcelSheet();
		
		System.out.println("Successfully created excel sheet on hard drive with name: " 
						+ _excelFileName);
	}
	
	/**
	 * Creates excel file on hard disk with given file name, sheet name, and at the given path.
	 * @param excelFileName
	 * @param excelSheetName
	 * @param filePath can be left null if you want to use the program's default.
	 */
	public static void createExcelFile(final String excelFileName, final String excelSheetName, 
			final String filePath)
	{
		String copiedFileName = excelFileName;
		
		// If file name is not formatted correctly.
		if(!copiedFileName.contains(".xlsx")) 
		{ 
			copiedFileName += ".xlsx"; 
			System.out.println("Warning: Excel file " + excelFileName + " string not formated correctly! \n"
					+ "Warning: Missing \".xlsx\" at end of excel file string.\n");
		}
		//else { System.out.println("Excel file " + copiedFileName + " does not exist!"); }
		
		System.out.println("Creating excel file...");
		
		// Save file path.
		_pathToFile = (filePath == null ? TestAutomation_Runner.EXCEL_FILE_FOLDER_PATH : filePath) + copiedFileName;
		
		_excelWorkbook = new XSSFWorkbook();
		_excelSheet = _excelWorkbook.createSheet(excelSheetName);
		
		// Write newly created excel file to hard disk.
		try 
		{
			FileOutputStream fileOut = new FileOutputStream(_pathToFile);
			_excelWorkbook.write(fileOut);
			fileOut.flush();
			fileOut.close();
		} 
		catch (Exception e2) 
		{
			e2.printStackTrace();
			return;
		}
		
		System.out.println("Succesfully created excel file: " + copiedFileName);
		
		loadDataContainerValues();
		createHeaderColumns();		
		fillSheetWithData();
		
		//Write initial data to file.
		writeBufferedDataToExcelSheet();
		
		// Format the excel sheet.
		formatExcelSheet();
		
		//Write formated cells to file.
		writeBufferedDataToExcelSheet();
	}
	
	/**
	 * Takes all the buffered data in the Excel workbook and writes it out to the
	 * Excel file on the hard drive.
	 */
	private static void writeBufferedDataToExcelSheet()
	{
		// Write data to excel file.
		try 
		{
			FileOutputStream fileOut = new FileOutputStream(_pathToFile);
			_excelWorkbook.write(fileOut);
			fileOut.flush();
			fileOut.close();
		} 
		catch (Exception e) 
		{
			System.out.println("An error occured when writing to excel file: ");
			e.printStackTrace();
		}
	}
	
	private static void clearExcelSheet()
	{
		try
		{
			// If able to get selected sheet go ahead and delete it.
			_excelWorkbook.getSheet(_excelSheetName);
		}
		catch(Exception e)
		{
			// If excel sheet is active, print stack trace.
			e.printStackTrace();
			//System.out.println("Not able to clear sheet since the desired sheet is active.");
		}
		
		_excelWorkbook.removeSheetAt(0);
		_excelSheet = _excelWorkbook.createSheet("Sheet1");
	}
	
	/** Loads all the global data containers with values. */
	private static void loadDataContainerValues()
	{
		// Used to keep count of how may categories will written to excel file.
		int count = 0;
		String[] categories = new String[9];
		
		if(ADD_GAME_INFO) 
		{
			LIST_CATEGORY_ISSUES.put(Data_Warehouse.GAME_INFO, Data_Warehouse.GAME_INFORMATION_ISSUES);
			categories[count] = Data_Warehouse.GAME_INFO;
			++count;
		}
		if(ADD_CODES_AND_PROMO) 
		{
			LIST_CATEGORY_ISSUES.put(Data_Warehouse.CODES_AND_PROMO, Data_Warehouse.CODES_AND_PROMO_ISSUES);
			categories[count] = Data_Warehouse.CODES_AND_PROMO;
			++count;
		}
		if(ADD_MANAGE_MY_ACCNT) 
		{
			LIST_CATEGORY_ISSUES.put(Data_Warehouse.MANAGE_MY_ACCNT, Data_Warehouse.MANAGE_MY_ACCOUNT_ISSUES);
			categories[count] = Data_Warehouse.MANAGE_MY_ACCNT;
			++count;
		}
		if(ADD_MISSING_CONTENT)
		{
			LIST_CATEGORY_ISSUES.put(Data_Warehouse.MISSING_CONTENT, Data_Warehouse.MISSING_CONTENT_ISSUES);
			categories[count] = Data_Warehouse.MISSING_CONTENT;
			++count;
		}
		if(ADD_ORDERS)
		{
			LIST_CATEGORY_ISSUES.put(Data_Warehouse.ORDERS, Data_Warehouse.ORDERS_ISSUES);
			categories[count] = Data_Warehouse.ORDERS;
			++count;
		}
		if(ADD_REPORT_BUG)
		{
			LIST_CATEGORY_ISSUES.put(Data_Warehouse.REPORT_BUG, Data_Warehouse.REPORT_BUG_ISSUES);
			categories[count] = Data_Warehouse.REPORT_BUG;
			++count;
		}
		if(ADD_REPORT_HARASSMENT)
		{
			LIST_CATEGORY_ISSUES.put(Data_Warehouse.REPORT_HARASSMENT, Data_Warehouse.REPORT_HARASSMENT_ISSUES);
			categories[count] = Data_Warehouse.REPORT_HARASSMENT;
			++count;
		}
		if(ADD_TECHNICAL_SUPPORT)
		{
			LIST_CATEGORY_ISSUES.put(Data_Warehouse.TECHNICAL_SUPPORT, Data_Warehouse.TECHNICAL_SUPPORT_ISSUES);
			categories[count] = Data_Warehouse.TECHNICAL_SUPPORT;
			++count;
		}
		if(ADD_WARRANTY)
		{
			LIST_CATEGORY_ISSUES.put(Data_Warehouse.WARRANTY, Data_Warehouse.WARRANTY_ISSUES);
			categories[count] = Data_Warehouse.WARRANTY;
			++count;
		}
		
		_activeCategories = new String[count];
		
		// Assign all strings from temporary array to class array.
		for(int i = 0; i < count; ++i)
		{
			_activeCategories[i] = categories[i];
		}
	}

	/**
	 * Creates the headers for the excel sheet(ex. Product, Category, Issues, Locale).
	 */
	private static void createHeaderColumns()
	{
		// Create first row of sheet.
		XSSFRow row = _excelSheet.createRow(0);
		
		// Create cells.
		for(int i = 0; i < Data_Warehouse.EXCEL_NUM_COLS; ++i) { row.createCell(i); }
		
		// Write headers of the first row of the sheet.
		row.getCell(Data_Warehouse.EXCEL_PRODUCT_COL).setCellValue("PRODUCT");
		row.getCell(Data_Warehouse.EXCEL_CATEGORY_COL).setCellValue("CATEGORY");
		row.getCell(Data_Warehouse.EXCEL_ISSUE_COL).setCellValue("SUB CATEGORY");
		row.getCell(Data_Warehouse.EXCEL_LOCALE_COL).setCellValue("LOCALE");
		row.getCell(Data_Warehouse.EXCEL_CHAT_COL).setCellValue("CHAT");
		row.getCell(Data_Warehouse.EXCEL_PHONE_COL).setCellValue("PHONE");
		row.getCell(Data_Warehouse.EXCEL_EMAIL_COL).setCellValue("EMAIL");
		row.getCell(Data_Warehouse.EXCEL_AHQ_COL).setCellValue("AHQ");
		row.getCell(Data_Warehouse.EXCEL_OFF_HOUR_COL).setCellValue("OFF HOURS");
		row.getCell(Data_Warehouse.EXCEL_ERROR_COL).setCellValue("ERRORS");
	}
	
	/**
	 * Applies any formats to the excel sheet such as widening a column or adding bold text
	 * to a cell.
	 */
	private static void formatExcelSheet()
	{
		int prod_col_maxNumChars = _excelSheet.getRow(0).getCell(Data_Warehouse.EXCEL_PRODUCT_COL)
				.getStringCellValue().length();
		int categ_col_maxNumChars = _excelSheet.getRow(0).getCell(Data_Warehouse.EXCEL_CATEGORY_COL)
				.getStringCellValue().length();
		int issue_col_maxNumChars = _excelSheet.getRow(0).getCell(Data_Warehouse.EXCEL_ISSUE_COL)
				.getStringCellValue().length();
		int error_col_maxNumChars = 110;
		int lastRow = 0;
		float maxCharWidth = 1f;
		int numFontUnits = 256;
		
		// Get the last row in the sheet.
		lastRow = _excelSheet.getLastRowNum();
		
		int i = 1;
		
		try
		{
			// Find the largest string in each given column.
			for(i = 1; i < lastRow; ++i)
			{
				// Get the length of each string value in production column.
				if(prod_col_maxNumChars < _excelSheet.getRow(i).getCell(Data_Warehouse.EXCEL_PRODUCT_COL)
						.getStringCellValue().length())
				{
					prod_col_maxNumChars = _excelSheet.getRow(i).getCell(Data_Warehouse.EXCEL_PRODUCT_COL)
							.getStringCellValue().length();
				}
				
				if(categ_col_maxNumChars < _excelSheet.getRow(i).getCell(Data_Warehouse.EXCEL_CATEGORY_COL)
						.getStringCellValue().length())
				{
					categ_col_maxNumChars = _excelSheet.getRow(i).getCell(Data_Warehouse.EXCEL_CATEGORY_COL)
							.getStringCellValue().length();
				}
				
				if(issue_col_maxNumChars < _excelSheet.getRow(i).getCell(Data_Warehouse.EXCEL_ISSUE_COL)
						.getStringCellValue().length())
				{
					issue_col_maxNumChars = _excelSheet.getRow(i).getCell(Data_Warehouse.EXCEL_ISSUE_COL)
							.getStringCellValue().length();
				}
			}
		}
		catch(Exception e){
			System.out.println("Crash on index: " + i);
		}
		
		// Set width of columns.
		_excelSheet.setColumnWidth(Data_Warehouse.EXCEL_PRODUCT_COL, (int)(prod_col_maxNumChars * maxCharWidth) 
				* numFontUnits);
		_excelSheet.setColumnWidth(Data_Warehouse.EXCEL_CATEGORY_COL, (int)(categ_col_maxNumChars * maxCharWidth) 
				* numFontUnits);
		_excelSheet.setColumnWidth(Data_Warehouse.EXCEL_ISSUE_COL, (int)(issue_col_maxNumChars * maxCharWidth) 
				* numFontUnits);
		_excelSheet.setColumnWidth(Data_Warehouse.EXCEL_OFF_HOUR_COL, (int)(10 * maxCharWidth) * numFontUnits);
		_excelSheet.setColumnWidth(Data_Warehouse.EXCEL_ERROR_COL, (int)(error_col_maxNumChars * maxCharWidth)
				* numFontUnits);
		
		// Create bold text font.
		XSSFFont boldText = _excelWorkbook.createFont();
		XSSFCellStyle style = _excelWorkbook.createCellStyle();
		boldText.setBold(true);
		style.setFont(boldText);
		style.setAlignment(HorizontalAlignment.CENTER);
		
		// Assign style to header row.
		for(i = 0; i < _excelSheet.getRow(0).getLastCellNum(); ++i)
		{
			_excelSheet.getRow(0).getCell(i).setCellStyle(style);
		}
		
		// Create cell style for error column.
		XSSFCellStyle errCellStyle = _excelWorkbook.createCellStyle();
		XSSFFont redFont = _excelWorkbook.createFont();
		redFont.setColor(HSSFColor.RED.index);
		errCellStyle.setFont(redFont);
		errCellStyle.setWrapText(true);
		
		// Apply format to error column.
		for(i = 1 ; i < lastRow; ++i)
		{
			_excelSheet.getRow(i).getCell(Data_Warehouse.EXCEL_ERROR_COL).setCellStyle(errCellStyle);
		}
	}

	/**
	 * Fill the excel sheet with data such as game titles, categories, issues, locales
	 * in respective columns.
	 */
	private static void fillSheetWithData()
	{
		// Determines which row to begin writing values to.
		int rowIndex = 1;
		
		// Object that represents an excel row.
		XSSFRow row;
		
		// For every product.
		for(int i = 0; i < _products.length; ++i)
		{
			// For every category.
			for(int j = 0; j < LIST_CATEGORY_ISSUES.size(); ++j)
			{
				// For every issue in the category.
				for(int k = 0; k < LIST_CATEGORY_ISSUES.get(_activeCategories[j]).length; ++k)
				{
					// For every locale.
					for(int m = 0; m < _locales.length; ++m)
					{
						// Create new row and increment row index.
						row = _excelSheet.createRow(rowIndex++);
						
						// Create cells.
						for(int n = 0; n < Data_Warehouse.EXCEL_NUM_COLS; ++n) {row.createCell(n);}
						
						// Fill current row with data.
						row.getCell(Data_Warehouse.EXCEL_PRODUCT_COL).setCellValue(_products[i]);
						row.getCell(Data_Warehouse.EXCEL_CATEGORY_COL).setCellValue(_activeCategories[j]);
						row.getCell(Data_Warehouse.EXCEL_ISSUE_COL).setCellValue(LIST_CATEGORY_ISSUES.get(_activeCategories[j])[k]);
						row.getCell(Data_Warehouse.EXCEL_LOCALE_COL).setCellValue(Data_Warehouse.MAP_LOCALE.get(_locales[m]));
						row.getCell(Data_Warehouse.EXCEL_CHAT_COL).setCellValue("OFF");
						row.getCell(Data_Warehouse.EXCEL_PHONE_COL).setCellValue("OFF");
						row.getCell(Data_Warehouse.EXCEL_EMAIL_COL).setCellValue("OFF");
						row.getCell(Data_Warehouse.EXCEL_AHQ_COL).setCellValue("OFF");
						row.getCell(Data_Warehouse.EXCEL_OFF_HOUR_COL).setCellValue("OFF");
						row.getCell(Data_Warehouse.EXCEL_ERROR_COL).setCellValue(" ");
					}
				}
			}
		}
		
		// Shift all rows down 1 row starting with row 2.
		_excelSheet.shiftRows(1, _excelSheet.getLastRowNum() + 1, 1);
		
		// After shifting all rows down. Create a new row on row 2.
		_excelSheet.createRow(1);
		
		// Create new cells on row 2.
		for(int i = 0; i < Data_Warehouse.EXCEL_NUM_COLS; ++i) {_excelSheet.getRow(1).createCell(i);}
		
		// Place given string in first cell of row 2.
		_excelSheet.getRow(1).getCell(0).setCellValue("START_ROW");
		
		// Create new row after the current last row in the excel sheet.
		_excelSheet.createRow(_excelSheet.getLastRowNum() + 1);
		
		// Create new cells on the new last row.
		for(int i = 0; i < Data_Warehouse.EXCEL_NUM_COLS; ++i) {_excelSheet.getRow(_excelSheet.getLastRowNum()).createCell(i);}
		
		// Add given string to the last row of the excel sheet.
		_excelSheet.getRow(_excelSheet.getLastRowNum()).getCell(0).setCellValue("END_ROW");
	}
}
