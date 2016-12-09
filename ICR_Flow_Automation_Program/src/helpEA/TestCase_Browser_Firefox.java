package helpEA;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class TestCase_Browser_Firefox extends TestCase_Browser 
{
	private WebDriver _driver = new FirefoxDriver();
	
	public TestCase_Browser_Firefox()
	{
		// Call super class constructor.
		super();
	}
	
	public TestCase_Browser_Firefox(final String loginEmail, final String loginPassword)
	{
		// Call super class constructor.
		super(loginEmail, loginPassword);
	}

	@Override
	public WebDriver getWebDriver() { return _driver; }

}
