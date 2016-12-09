package helpEA;

import java.util.List;
import java.util.Random;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class EA_Help_Automation_Helper 
{
	/** Random number generator. */
	private static Random _rng;
	
	/** Reference to thread that is running cases. */
	private TestCase_Run _caseRunner;
	
	// Flag to determine which ICR the program is in.
	private boolean _isOldICR = false;
	
	/** This array contains string values of all categories. */
	private static final String[] CATEGORY_OPTIONS = 
		{
			Data_Warehouse.CODES_AND_PROMO, 
			Data_Warehouse.GAME_INFO, Data_Warehouse.MANAGE_MY_ACCNT, 
			Data_Warehouse.MISSING_CONTENT, Data_Warehouse.ORDERS, 
			Data_Warehouse.REPORT_BUG, Data_Warehouse.REPORT_HARASSMENT, 
			Data_Warehouse.TECHNICAL_SUPPORT, Data_Warehouse.WARRANTY
		};
	
	/** Returns a flag determining if the ICR flow is the old ICR. */
	public boolean getIsOldICR() { return _isOldICR; }
	
	public EA_Help_Automation_Helper(TestCase_Run caseRunner)
	{
		_rng = new Random();
		_caseRunner = caseRunner;
	}
	
	// Reset values of all class variables.
	public void resetValues() { _isOldICR = false; }
	
	private void closePreviousCasesPopup(final WebDriver driver)
		throws Exception
	{
		long waitedTime = 0;
		short timeToSleep = 2000;
		long elapsedTime = 0;
		long timeOutTime = 5;
		long startTime = 0;
		
		Thread.sleep(timeToSleep);
		waitedTime += timeToSleep;
		
		// Old ICR previous case popup.
		// Attempts to close previous case pop up. 
		// There may be a scenario where the current account has no case
		// so I will catch exception and do nothing.
		try
		{
			driver.findElement(By.cssSelector("a.btn-next")).click();
			_isOldICR = true;
			Thread.sleep(1000);
		}
		catch(Exception e) // Try to close new ICR pop up.
		{
			try
			{
				// Wait for pop up screen to display.
				while(!driver.findElement(By.cssSelector("span.wwce-close")).isDisplayed())
				{
					startTime = System.currentTimeMillis();
					
					// If pop up window is displayed.
					if(driver.findElement(By.cssSelector("span.wwce-close")).isDisplayed()) { break; }
					
					// This is to prevent infinite loop.
					if(elapsedTime > timeOutTime) { break; /* Break out of loop.*/ }
					
					elapsedTime += (System.currentTimeMillis() - startTime);
				}
				
				// Reset timer.
				elapsedTime = 0;
				
				// While pop-up window is still visible.
				while(driver.findElement(By.cssSelector("span.wwce-close")).isDisplayed())
				{
					startTime = System.currentTimeMillis();
					
					Thread.sleep(timeToSleep);
					waitedTime += timeToSleep;
					
					// If pop-up x button is visible.
					if(driver.findElement(By.cssSelector("div.wwce-modal-content > "
						+ "div.wwce-modal-header > span.wwce-close")).isDisplayed())
					{
						driver.findElement(By.cssSelector("div.wwce-modal-content > "
								+ "div.wwce-modal-header > span.wwce-close")).click();
					}
					
					// If pop-up window is not visible.
					if(!driver.findElement(By.cssSelector("span.wwce-close")).isDisplayed()) { break; }
					
					// This is to prevent infinite loop.
					if(elapsedTime > timeOutTime) 
					{ 
						System.out.println("TEST CASE FAILED: Not able to close pop up window!");
						throw new Exception("TEST CASE FAILED: Not able to close pop up window!");
						//break; /* Break out of loop.*/
					}
					
					elapsedTime += (System.currentTimeMillis() - startTime);
				}
				
				Thread.sleep(timeToSleep);
				waitedTime += timeToSleep;
			}
			catch(Exception e1) {/*DO NOTHING*/}
		}
	}
	
	public void selectGame(final WebDriver driver, final String gameTitle,
			final boolean hasLoggedIn) 
			throws Exception
	{
		long waitedTime = 0;
		short timeToSleep = 2000;
		
		// If logged in.
		if(hasLoggedIn)
		{
			try { closePreviousCasesPopup(driver); }
			catch(Exception e) 
			{ 
				e.printStackTrace(); 
				throw new Exception(e.getMessage());
			}
		}
		
		try { selectIsSpecificGameOption(driver); }
		catch(Exception e) { /*throw new Exception(e.getMessage()); */}
		
		// Trying to enter game title into new ICR Flow
		try
		{
			// Enter title of game into game search bar.
			driver.findElement(By.cssSelector("div.game-search > input#search-input")).sendKeys(
					gameTitle);
			Thread.sleep(timeToSleep);
			waitedTime += timeToSleep;
		}
		catch(Exception e)
		{	
			// Try to search for game in old ICR.
			try
			{
				// Enter title of game into search bar.
				driver.findElement(By.cssSelector("div.game-filter > input")).sendKeys(gameTitle);
				
				// Flag that program is on the old ICR flow.
				_isOldICR = true;
			}
			catch(Exception e2) 
			{
				// Print error message to console.
				e2.printStackTrace();
				
				// Throw new exception with both new and old ICR error messages.
				throw new Exception("NEW ICR ERROR: " + e.getMessage() + "\nOLD ICR ERROR: " 
								+ e2.getMessage());
			}
		}
		
		Thread.sleep(1000);
		waitedTime += 1000;
		
		// Select Game.
		try
		{
			if(_isOldICR)
			{
				if(gameTitle.equals("The Sims 3") || gameTitle.equals("the-sims-3"))
				{
					WebElement rightArrow = driver.findElement(
							By.cssSelector("span.icon-chevron-right"));
					
					// Navigate right.
					for(int i = 0; i < 5; ++i)
					{
						rightArrow.click();
						Thread.sleep(500);
					}
					
					// Click on game tile.
					//el.getAttribute("data-model").contains(locale)
					List<WebElement> gameTiles = driver.findElements(By.cssSelector("ul.carousel > li"));
					
					for(WebElement gameTile : gameTiles)
					{
						if(gameTile.getAttribute("data-model").contains(gameTitle))
						{
							// Click on game tile.
							gameTile.click();
						}
					}
				}
				else // For all other games.
				{
					// Click on game tile.
					driver.findElement(By.cssSelector("li.item.no-fx > "
							+ "a[title='" + gameTitle + "'] > span")).click();
					
					Thread.sleep(1000);
					waitedTime += 1000;
					
					// Click "Next" Button.
					driver.findElement(By.cssSelector("a.btn-next")).click();
				}
			}
			else // If new ICR.
			{
				if(gameTitle.equals("The Sims 3") || gameTitle.equals("the-sims-3"))
				{
					// Get all Sims 3 games.
					List<WebElement> gameSelectionOptions = driver.findElements(By.cssSelector(
							"div#contact > div.product-container > "
							+ "div[wwce-productcts=''] > div.product-grid > ea-grid > "
							+ "ea-grid-items > ea-grid-item"));
					Thread.sleep(500);
					waitedTime = 500;
				
					// Click on game based on index.
					gameSelectionOptions.get(1).click();
				}
				else
				{
					// Click on game.
					driver.findElement(By.cssSelector("div.product-grid > ea-grid > ea-grid-items > "
							+ "ea-grid-item[title='" + gameTitle + "'] > "
							+ "wwce-ea-tile > div > div")).click();
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new Exception(e);
		}
		
		Thread.sleep(timeToSleep);
		waitedTime += timeToSleep;
	}
	
	public void selectCategory(final WebDriver driver, final String category) 
			throws Exception
	{
		int waitedTime = 0;
		short timeToSleep = 2000;
		boolean foundTopic = false;
		
		if(_isOldICR)
		{
			String generatedID = driver.findElement(By.cssSelector(
					"select[name='category']")).getAttribute("id");
			
			// Select category drop down.
			driver.findElement(By.cssSelector("div#" + generatedID + "_chzn")).click();
			
			List<WebElement> categoryOptions = driver.findElements(By.cssSelector(
					"div#" + generatedID + "_chzn > div > ul > li"));
			
			Thread.sleep(2000);
			waitedTime += 2000;
			
			for(WebElement cat : categoryOptions)
			{
				if(cat.getText().contains(category)) 
				{ 
					cat.click(); 
					break;
				}
			}
		}
		else // If new ICR
		{
			List<WebElement> topicOptions = driver.findElements(By.cssSelector(
					"div#step3 > div.content > div.item-list > div.item > a"));
			
			for(WebElement el : topicOptions)
			{
				// If current topic is equal to the passed in topic string click on it.
				if(el.getAttribute("data-model").contains(category))
				{
					foundTopic = true;
					el.click();
					break;
				}
			}
			
			if(!foundTopic)
			{
				System.out.println("TEST CASE FAILED: Topic does not exist "
						+ "for this game and platform combination.");
				throw new Exception("TEST CASE FAILED: Topic does not exist "
						+ "for this game and platform combination.");
			}
			
			// Check if there are any localization discrepancies.
			// If not an English locale.
			if(!isEnglishLocale(_caseRunner.getCurrentLocale()))
			{
				for(WebElement el : topicOptions)
				{
					for(String categOption : CATEGORY_OPTIONS)
					{
						// If the localized string is equal to the English locale.
						if(el.findElement(By.cssSelector("div > span")).getText().equals(categOption))
						{
							System.out.println("LOCALIZATION ERROR FOR CATEGORY OPTION(" + 
									Data_Warehouse.MAP_LOCALE.get(
											_caseRunner.getCurrentLocale()) +"): " + categOption);
							
							// Write error to test case result.
							_caseRunner.getBrowser().getTestCaseResult().setErrorMessage(
									"LOCALIZATION ERROR FOR CATEGORY OPTION(" + 
									Data_Warehouse.MAP_LOCALE.get(
											_caseRunner.getCurrentLocale()) +"): " + categOption);
						}
					}
				}
			}
		}
		
		Thread.sleep(timeToSleep);
		waitedTime += timeToSleep;
	}
	
	public void selectPlatform(final WebDriver driver) 
			throws Exception
	{
		int waitedTime = 0;
		short timeToSleep = 2000;
		
		Thread.sleep(timeToSleep);
		waitedTime += timeToSleep;
		
		if(_isOldICR)
		{
			int numTilesDispAtOnce = 5;
			
			// Stores a list of all the platform options
			List<WebElement> platformOptions = driver.findElements(By.cssSelector(
					"ul.carousel > li"));
			
			// Randomly select platform.
			int index = 0;
			while(index == 0){ index = _rng.nextInt(platformOptions.size()); }
			
			if(index > numTilesDispAtOnce - 1)
			{
				// Click right arrow.
				driver.findElement(By.cssSelector("span.icon-chevron-right")).click();
				
				Thread.sleep(1000);
				waitedTime += 1000;
			}
			
			// Click platform tile.
			platformOptions.get(index).click();
			
			Thread.sleep(1000);
			waitedTime += 1000;
			
			// Click "Next" Button.
			driver.findElement(By.cssSelector("a.btn-next")).click();
		}
		else // If new ICR.
		{
			List<WebElement> platformOptions = driver.findElements(By.cssSelector(
					"wwce-container.platform-container > div > "
					+ "div > div.platform-content > ea-grid > ea-grid-items > ea-grid-item"));
			
			// Randomly select platform.
			int index = 0;
			while(index == 0){ index = _rng.nextInt(platformOptions.size()); }
		
			// Click on platform option.
			platformOptions.get(index).click();
		}
		
		Thread.sleep(timeToSleep);
		waitedTime += timeToSleep;
	}
	
	public void selectIssue(final WebDriver driver, final String issue)
		throws Exception
	{
		short timeToSleep = 3000;
		int waitedTime = 0;
		boolean foundIssue = false;
		
		Thread.sleep(1000);
		waitedTime += 1000;
		
		try
		{
			if(_isOldICR)
			{
				System.out.println("ODL ICR ISSUE SELECTOR");
				
				String generatedID = driver.findElement(By.cssSelector(
						"select[name='subcategory']")).getAttribute("id");
				
				// Click on sub category drop down.
				driver.findElement(By.cssSelector("div#" + generatedID + "_chzn")).click();
				
				List<WebElement> subCatOptions = driver.findElements(By.cssSelector(
						"div#" + generatedID + "_chzn > div > ul > li"));
				
				Thread.sleep(2000);
				waitedTime += 2000;
				
				for(WebElement subCat : subCatOptions)
				{
					System.out.println("issue: " + subCat.getText());
					System.out.println("issue id: " + subCat.getAttribute("id"));
				}
				
				System.out.println("Attempting to click on issue");
				
				for(WebElement subCat : subCatOptions)
				{
					if(subCat.getText().contains(issue)) 
					{ 
						System.out.println("Issue to click: " + subCat.getText()); 
						driver.findElement(By.cssSelector("li#" + subCat.getAttribute("id"))).click();
					}
					
					break;
				}
				System.out.println("Click on issue.");
				
				Thread.sleep(500);
				waitedTime += 500;
				
				// Fill out "More info about issue" text field.
				driver.findElement(By.cssSelector(
						"div.item.required.error > label > input")).sendKeys("test");
				
				Thread.sleep(500);
				waitedTime += 500;
			}
			else // Is new ICR.
			{
				// Store all issue options in list.
				List<WebElement> issueListOptions = driver.findElements(
						By.cssSelector("wwce-container.issue-container > "
								+ "div[wwce-issues=''] > div > a"));
				
				// WebDriver will occasionally return duplicates of elements but with empty strings.
				// I am parsing the list so not to accidentally select a duplicate element
				// and cause the program to crash.
				for(int i = 0; i < issueListOptions.size(); ++i)
				{
					// Check to see if any of the elements have empty text.
					if(issueListOptions.get(i).getText().equals(""))
					{
						issueListOptions.remove(i); // Delete element with empty text.
						i = 0; // Reset loop.
					}
				}
				
				// If no issue was passed into function randomly select an issue.
				if(issue.equals("") || issue == null)
				{
					// Randomly select issue.
					int index = 0;
					while(index == 0) { index = _rng.nextInt(issueListOptions.size()); }
					
					// Click on issue option.
					issueListOptions.get(index).click();
				}
				else
				{
					// Save copy of issue string for manipulation.
					String cpy_str_issue = new String(issue);
					
					for(WebElement el : issueListOptions)
					{
						try
						{
							if(el.findElement(By.cssSelector("div > span")).getText().contains(issue))
							{
								foundIssue = true;
								el.click();
								break;
							}
						}
						catch(Exception e)
						{
							if(el == null) { System.out.println("The web element is null."); }
						}
					}
				}
				
				// Holds all the issues for the current category.
				String[] issues = null;
				
				// If issue was not found.
				if(!foundIssue)
				{
					System.out.println("TEST CASE FAILED: Issue does not exist "
							+ "for this game and topic combination.");
					throw new Exception("TEST CASE FAILED: Issue does not exist "
							+ "for this game and topic combination.");
				}
				
				// Check if there are any localization discrepancies.
				// If not an English locale.
				if(!isEnglishLocale(_caseRunner.getCurrentLocale()))
				{
					switch(_caseRunner.getCurrentCategory())
					{
						case "Codes and promotions":
							issues = Data_Warehouse.CODES_AND_PROMO_ISSUES;
							break;
						case "Game information":
							issues = Data_Warehouse.GAME_INFORMATION_ISSUES;
							break;
						case "Manage my account":
							issues = Data_Warehouse.MANAGE_MY_ACCOUNT_ISSUES;
							break;
						case "Missing content":
							issues = Data_Warehouse.MISSING_CONTENT_ISSUES;
							break;
						case "Orders":
							issues = Data_Warehouse.ORDERS_ISSUES;
							break;
						case "Report a bug":
							issues = Data_Warehouse.REPORT_BUG_ISSUES;
							break;
						case "Report concerns or harassment":
							issues = Data_Warehouse.REPORT_HARASSMENT_ISSUES;
							break;
						case "Technical support":
							issues = Data_Warehouse.TECHNICAL_SUPPORT_ISSUES;
							break;
						case "Warranty":
							issues = Data_Warehouse.WARRANTY_ISSUES;
							break;
					}
					for(WebElement el : issueListOptions)
					{
						for(String curIssue : issues)
						{
							// If the localized string is equal to the English locale.
							if(el.findElement(By.cssSelector("div > span")).getText().equals(curIssue))
							{
								System.out.println("LOCALIZATION ERROR FOR ISSUE OPTION("
										+ Data_Warehouse.MAP_LOCALE.get(_caseRunner.getCurrentLocale()) +"): " + curIssue);
								
								// Write error to test case result.
								_caseRunner.getBrowser().getTestCaseResult().setErrorMessage("LOCALIZATION ERROR FOR "
										+ "ISSUE OPTION(" + Data_Warehouse.MAP_LOCALE.get(_caseRunner.getCurrentLocale()) +"): " + curIssue);
							}
						}
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		
		Thread.sleep(timeToSleep);
		waitedTime += timeToSleep;
	}
	
	/**
	 * In the ICR flow, there is an option where if you have not selected a product in the flow
	 * it will prompt the user on the "contact us" screen with an option on whether the 
	 * issue involves a specific game. This is done before product selection if the user
	 * clicked "contact us" on the EA Help home page.
	 * @param driver
	 * @throws Exception
	 */
	public void selectIsSpecificGameOption(final WebDriver driver) throws Exception
	{
		// Click on yes button signifying that it is a specific game.
		try { driver.findElement(By.cssSelector("a#isGameRelatedQuery > div > span")).click(); }
		catch(Exception e) { /*DO NOTHING*/ }
	}

	/**
	 * Game specific options are selections that involve: Pogo, Origin.
	 * @param driver
	 * @param gameTitle
	 * @throws InterruptedException there are sleep method calls in this method to 
	 * 			allow web page load
	 */
	public void selectGameSpecificOptions(final WebDriver driver, final String gameTitle) 
			throws Exception
	{
		short timeToSleep = 2000;
		System.out.println("Executing Game Specific Options code...");
		// Select Pogo options
		try
		{
			if(gameTitle.equals("Pogo") || gameTitle.equals("pogo"))
			{
				if(_isOldICR)
				{
					System.out.println("OLD ICR FLOW...");
					
					// Obtain generated ID.
					String generatedID = driver.findElement(By.cssSelector("div.voc-fields > div > "
							+ "div > div > div > div")).getAttribute("id");
					
					System.out.println("Generated ID: " + generatedID);
					
					// Click drop down box.
					driver.findElement(By.cssSelector(
							"div.voc-fields > div > div > div > div > div")).click();
					
					Thread.sleep(2000);
					
					// Select "No" from issue with specific game drop-down menu.
					driver.findElement(By.cssSelector(
							"div.voc-fields > div > div > div > div > div > div > "
							+ "ul > li#" + generatedID + "_o_2")).click();
				}
				else
				{
					// Grab the id that is connected to the select tag on the drop box that is
					// generated whenever the page is loaded.
					String pogoSelectID = driver.findElement(
							By.cssSelector("div.vog-component-container > div[wwce-vog=''] > "
									+ "ea-text > div.voc-fields-new > div.default.field-list > "
									+ "div > div > div.field.dropdown > select")).getAttribute("id");
					
					// Use the id to get access to the clickable part of the drop box.
					WebElement pogoDropDownBox = driver.findElement(
							By.cssSelector("div#" + pogoSelectID + "_chzn"));
					pogoDropDownBox.click();
					Thread.sleep(timeToSleep);
					
					// Store all platform drop-box's web elements (list options).
					List<WebElement> originListOptions = pogoDropDownBox.findElements(
							By.cssSelector("div.chzn-drop > ul.chzn-results > li"));
					
					// Loop through all list options to find matching string.
					for(WebElement el : originListOptions)
					{
						if(el.getText().equals("No"))
						{
							// Click on web element with matching string.
							driver.findElement(By.id(el.getAttribute("id"))).click();
							break;
						}
					}
				}
			}
			
			Thread.sleep(timeToSleep);
		}
		catch(Exception e) { e.printStackTrace(); throw new Exception(e.getMessage()); }
		
		// Select Origin options.
		try
		{
			if(gameTitle.equals("Origin") || gameTitle.equals("origin"))
			{
				if(!_caseRunner.getCurrentCategory().equals("Missing content") || !_caseRunner.getCurrentCategory().equals("Orders"))
				{
					// Click on Origin version.
					driver.findElement(By.cssSelector("span.radio-span")).click(); 
					Thread.sleep(timeToSleep);
				}
				else if(_caseRunner.getCurrentCategory().equals("Missing content"))
				{
					String generatedID = driver.findElement(By.cssSelector(
							".row.a3vE0000000CvHuIAK.dropdown.Q336069.error.required"
							+ " > div > div > div")).getAttribute("id");
					
					// Select is issue related to specific game.
					driver.findElement(By.cssSelector(
							"div.row.a3vE0000000CvHuIAK.dropdown.Q336069.error.required"
							+ " > div > div > div > a")).click();
					Thread.sleep(500);
					
					// Select "No" from drop down.
					driver.findElement(By.cssSelector(
							"div.row.a3vE0000000CvHuIAK.dropdown.Q336069.error.required"
							+ " > div > div > div > div > ul > li#" + 
									generatedID + "_o_2")).click();
					Thread.sleep(500);
					
					// Select "What is missing?"
					generatedID = driver.findElement(By.cssSelector(
							"div.row.a3vE0000000CvHzIAK.dropdown.Q336070.error.required"
							+ " > div > div > div")).getAttribute("id");
					Thread.sleep(500);
					
					// Select "I'm missing a game"
					driver.findElement(By.cssSelector(
							"div.row.a3vE0000000CvHzIAK.dropdown.Q336070.error.required"
							+ " > div > div > div > div > ul > li#" + 
									generatedID + "_o_1")).click();
					Thread.sleep(500);
				}
			}
		}
		catch(Exception e1) { throw new Exception(e1.getMessage()); }
		
		// Select Star Wars: Galaxy of Heroes options.
		if(gameTitle.equals("Star Wars: Galaxy of Heroes"))
		{
			try
			{
				// Click connection drop down.
				driver.findElement(By.cssSelector("div.default.field-list > div > "
						+ "div > div.field.dropdown > div > a > span")).click();
				
				Thread.sleep(3000);
				
				// Click option.
				List<WebElement> options = driver.findElements(By.cssSelector("div.default.field-list > div > "
						+ "div > div.field.dropdown > div > div > ul > li"));
				options.get(1).click();
				
				Thread.sleep(timeToSleep);
			}
			catch(Exception e){}
		}
		
		Thread.sleep(timeToSleep);
	}

	/**
	 * This function will handle any VOG forms in the ICR Flow. 
	 * @param driver
	 **/
	public void fillVOG(final WebDriver driver) throws Exception
	{
		try
		{
			// Fill out text area.
			driver.findElement(By.cssSelector("textArea[type=text]")).sendKeys("UAT Test");
			
			// Click check box.
			driver.findElement(By.cssSelector("span.checkbox-list > label > input")).click();
		}
		catch(Exception e) { throw new Exception(e); }
		
		Thread.sleep(1000);
	}
	
	public void logIn(final WebDriver driver, final String email, final String password) 
			throws InterruptedException
	{
		// Save current URL.
		String URL_channelSelectPage = driver.getCurrentUrl();
		
		/* 
		 * First I am going to try to log in through the secured Help EA web-site log in page.
		 * This is the gray box with the login form that you need to fill out before 
		 * you are able to select a contact channel.
		 * If this fails, for whatever reason, I will log in by clicking the log in button at
		 * the top of the channel select page. The reason why I do not log in this was first 
		 * is because I have to do the same thing as the first attempt by re-directing myself 
		 * to the secured log in page.
		 */ 
		try
		{
			// Navigate to log in web-site.
			driver.navigate().to(driver.findElement(
					By.cssSelector("div.content > iframe")).getAttribute("src"));
			Thread.sleep(2000);
		}
		catch(Exception e) 
		{
			// Click on login button on top of the web page and login through there.
			try
			{
				List<WebElement> loginOption = driver.findElements(
						By.cssSelector("div#utility-bar-header > div#gus > ul > li"));
				
				// Navigate to log in web-site.
				driver.navigate().to(loginOption.get(0).findElement(By.cssSelector("a")).getAttribute("href"));
				Thread.sleep(2000);
			}
			catch(Exception ex) { ex.printStackTrace(); }
		}
		
		// Fill in log in form.
		driver.findElement(By.cssSelector("input#email")).sendKeys(email);
		driver.findElement(By.cssSelector("input#password")).sendKeys(password);
		
		// Click "Log In" button.
		driver.findElement(By.cssSelector("a#btnLogin > span > span")).click();
		Thread.sleep(2000);
		
		// Navigate to back to channel selection page.
		driver.navigate().to(URL_channelSelectPage);
		
		Thread.sleep(6000);
	}
	
	/**
	 * This function will check the available options for the user to select on 
	 * the channel selection page.
	 * @param driver
	 * @param testResult
	 */
	public void checkForContactOptions(final WebDriver driver, 
			final TestCase_Result testResult) throws Exception
	{
		// Check for contact options in the old ICR.
		if(_isOldICR)
		{
			final String channel_chat = "channel chat";
			final String channel_phone = "channel phone";
			final String channel_email = "channel email";
			
			int position = 0;
			
			// Check if there is a featured channel.
			try
			{
				String contactChannel = driver.findElement(By.cssSelector(
						"div.featured-channel > div")).getAttribute("class");
				
				switch(contactChannel)
				{
					case channel_chat:
						testResult.setChatPosition(1);
						testResult.setIsChatVisible(true);
						break;
					case channel_phone:
						testResult.setPhonePosition(1);
						testResult.setIsPhoneVisible(true);
						break;
					case channel_email:
						testResult.setEmailPosition(1);
						testResult.setIsEmailVisible(true);
						break;
				}
				
				position = 1;
				
			}
			catch(Exception e) { e.printStackTrace(); }
			
			// Check if there are other contact methods.
			try
			{
				// Get list of all other contact channels
				List<WebElement> channels = driver.findElements(By.cssSelector(
						"div.item-list"));
				
				for(WebElement channel : channels)
				{
					String contactOption = channel.getAttribute("class");
					
					switch(contactOption)
					{
						case channel_chat:
							testResult.setChatPosition(++position);
							testResult.setIsChatVisible(true);
							break;
						case channel_phone:
							testResult.setPhonePosition(++position);
							testResult.setIsPhoneVisible(true);
							break;
						case channel_email:
							testResult.setEmailPosition(++position);
							testResult.setIsEmailVisible(true);
							break;
					}
				}
			}
			catch(Exception e) { e.printStackTrace(); }
		}
		else // Check for contact options in the new ICR.
		{
			try
			{
				List<WebElement> contactOptions = driver.findElements(
						By.cssSelector("div.channel-box-container > div"));
				
				final String chat_className = "channel-box chat";
				final String chatActive_className = "channel-box chat active";
				final String phone_className = "channel-box phone";
				final String phoneActive_className = "channel-box phone active";
				final String email_className = "channel-box email";
				final String emailActive_className = "channel-box email active";
				
				for(int i = 0; i < contactOptions.size(); ++i)
				{
					String str_contactOption = contactOptions.get(i).getAttribute("class");
					int position = i + 1;
					
					switch (str_contactOption)
					{
						case chat_className:
							testResult.setChatPosition(position);
							testResult.setIsChatVisible(true);
							break;
						case chatActive_className: 
							testResult.setChatPosition(position);
							testResult.setIsChatVisible(true);
							break;
						case phone_className:
							testResult.setPhonePosition(position);
							testResult.setIsPhoneVisible(true);
							break;
						case phoneActive_className:
							testResult.setPhonePosition(position);
							testResult.setIsPhoneVisible(true);
							break;
						case email_className:
							testResult.setEmailPosition(position);
							testResult.setIsEmailVisible(true);
							break;
						case emailActive_className:
							testResult.setEmailPosition(position);
							testResult.setIsEmailVisible(true);
							break;
					}
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		Thread.sleep(1000);
	}
	
	public void checkForAHQOption(final WebDriver driver, 
			final TestCase_Result testResult) throws Exception
	{
		try
		{
			if(driver.findElement(By.cssSelector(
					"div.community.static-channel-bar")).isDisplayed())
			{
				testResult.setIsAHQVisible(true);
			}
		}
		catch(Exception e) {/*Do nothing.*/}
		
		Thread.sleep(1000);
	}
	
	public void createChatTicket(final WebDriver driver) throws Exception
	{
		// Try clicking the expand button. Chat channel may be collapsed if under
		// "Other Support Options".
		try
		{
			driver.findElement(By.cssSelector("div.channel-box.chat > "
					+ "div.channel-box-header > img.expand-icon")).click();
		}
		catch(Exception e1) { /*DO NOTHING*/ }
		
		Thread.sleep(1000);
		
		System.out.println("Filling out ticket.");
		// Fill out chat ticket subject.
		driver.findElement(By.cssSelector("div.row.required > input.B2")).sendKeys("UAT Automation Test");
		
		Thread.sleep(1000);
		
		System.out.println("Grabbing handles.");
		// Get reference to EA Help window.
		String eaHelpWindowHandle = driver.getWindowHandle();
		
		// Click submit button.
		driver.findElement(By.cssSelector("#btn-live-chat")).click();
		
		Thread.sleep(20000);
		
		// Grab reference to all windows open under current driver.
		Set<String> windowHandles = driver.getWindowHandles();
		
		System.out.println("Window Handles:");
		for(String handle : windowHandles)
		{
			if(!handle.equals(eaHelpWindowHandle)) { driver.switchTo().window(handle); }
		}
		
		Thread.sleep(900000);
		
		// Close currently focused window.
		driver.close();
		
		// Switch back to main window.
		driver.switchTo().window(eaHelpWindowHandle);
	}
	
	private boolean isEnglishLocale(final String str)
	{
		boolean isEnglishLocale = false;

		String[] engLocales = {"en_US", "au_AU", "en_CA", "en_IN", "en_NZ", "en_GB"};
		
		for(String locale : engLocales)
		{
			if(str.equals(locale)) { isEnglishLocale = true; break;}
		}
		
		return isEnglishLocale;
	}
}
