package helpEA;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class TestCase_Browser_Chrome extends TestCase_Browser 
{
	private WebDriver _driver;
	
	public TestCase_Browser_Chrome() 
	{
		// Call super class constructor
		super();
		
		_driver = new ChromeDriver();
	}
	
	public TestCase_Browser_Chrome(final String loginEmail, final String loginPassword)
	{
		// Call super class constructor.
		super(loginEmail, loginPassword);
		
		_driver = new ChromeDriver();
	}

	@Override
	public WebDriver getWebDriver() { return _driver; }
}
