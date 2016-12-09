package helpEA;

import java.util.ArrayList;
import java.util.List;

public class TestCase_Result 
{
	private int _threadNum;
	private int _caseNum;
	private String _product;
	private String _category;
	private String _issue;
	private String _locale;
	
	private boolean _isAHQVisible;
	private boolean _isChatVisible;
	private boolean _isPhoneVisible;
	private boolean _isEmailVisible;
	
	private int _chatPosition;
	private int _phonePosition;
	private int _emailPosition;
	
	private List<String> _errorMessage;
	
	public TestCase_Result()
	{
		_isAHQVisible = false;
		_isChatVisible = false;
		_isPhoneVisible = false;
		_isEmailVisible = false;
		
		_chatPosition = 0;
		_phonePosition = 0;
		_emailPosition = 0;
		
		_errorMessage = new ArrayList<String>(0);
	}
	
	public void setIsAHQVisible(final boolean flag) { _isAHQVisible = flag; }
	public void setIsChatVisible(final boolean flag) { _isChatVisible = flag; }
	public void setIsPhoneVisible(final boolean flag) { _isPhoneVisible = flag; }
	public void setIsEmailVisible(final boolean flag) { _isEmailVisible = flag; }
	
	public void setThreadNumber(final int num) { _threadNum = num; }
	public void setCaseNumber(final int num) { _caseNum = num; }
	public void setProduct(final String product) { _product = product; } 
	public void setCategory(final String category) { _category = category; }
	public void setIssue(final String issue) { _issue = issue; } 
	public void setLocale(final String locale) { _locale = locale; }
	 
	public void setChatPosition(final int pos) { _chatPosition = pos; } 
	public void setPhonePosition(final int pos) { _phonePosition = pos; }
	public void setEmailPosition(final int pos) { _emailPosition = pos; } 
	
	/**
	 * This function stores all passed String values into a list.
	 * If this function is called more than once it will append a carriage return
	 * to the passed String argument so not to have all error Strings become
	 * one large String when printed out.
	 * @param errMsg
	 */
	public void setErrorMessage(final String errMsg) 
	{	
		// Add error message to list.
		_errorMessage.add(errMsg + "\n"); 
	} 
	
	public boolean getIsAHQVisible() { return _isAHQVisible; } 
	public boolean getIsChatVisible() { return _isChatVisible; } 
	public boolean getIsPhoneVisble() { return _isPhoneVisible; } 
	public boolean getIsEmailVisble() { return _isEmailVisible; } 
	
	public int getThreadNumber() { return _threadNum; }
	public int getCaseNumber() { return _caseNum; }
	public String getProduct() { return _product; }
	public String getCategory() { return _category; }
	public String getIssue() { return _issue; }
	public String getLocale() { return _locale; } 
	
	public int getChatPostion() { return _chatPosition; } 
	public int getPhonePosition() { return _phonePosition; }
	public int getEmailPosition() { return _emailPosition; }
	
	/**
	 * This return a List that contains all the error messages that this test case
	 * has occured. Every String in this list will have a new line character appended to it.
	 * */
	public List<String> getErrorMessages() { return _errorMessage; }
	
	public void resetValues()
	{
		_isAHQVisible = false;
		_isChatVisible = false;
		_isPhoneVisible = false;
		_isEmailVisible = false;
		
		_chatPosition = 0;
		_phonePosition = 0;
		_emailPosition = 0;
		
		_errorMessage.clear();
	}
}
