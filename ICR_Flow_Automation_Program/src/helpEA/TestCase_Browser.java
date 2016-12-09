package helpEA;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public abstract class TestCase_Browser
{
	private TestCase_Result result;
	private boolean _isLoggedIn = false;
	
	private String _loginEmail;
	private String _loginPassword;
	
	private int _testSuiteExcelStartRow = 0;
	
	public TestCase_Browser() 
	{
		_loginEmail = new String("wwceuat+auto1@gmail.com");
		_loginPassword = new String("Test12!@");
		result = new TestCase_Result();
	}
	
	public TestCase_Browser(final String loginEmail, final String loginPassword)
	{
		_loginEmail = new String(loginEmail);
		_loginPassword = new String(loginPassword);
		result = new TestCase_Result();
	}
	
	public void setIsLoggedIn(final boolean flag) { _isLoggedIn = flag; } 
	public void setLoginEmail(final String email) { _loginEmail = email; }
	public void setLoginPassword(final String password) { _loginPassword = password; }
	public void setTestSuiteStartRow(final int startRow) { _testSuiteExcelStartRow = startRow; } 
	
	public boolean getIsLoggedIn() { return _isLoggedIn; } 
	public String getLoginEmail() { return _loginEmail; }
	public String getLoginPassword() { return _loginPassword; }
	public TestCase_Result getTestCaseResult() { return result; }
	public int getTestSuiteExcelStartRow() { return _testSuiteExcelStartRow; }
	public abstract WebDriver getWebDriver();
}
