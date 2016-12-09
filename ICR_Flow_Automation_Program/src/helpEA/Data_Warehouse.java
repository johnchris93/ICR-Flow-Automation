package helpEA;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Data_Warehouse 
{
	/**
	 * This data container holds all of the supported locales for this program. The was
	 * the key-value pairs for the container is structured is where the key is "Russia" and the
	 * value would be "ru_RU".
	 */
	public static final Map<String, String> MAP_LOCALE = Collections.synchronizedMap(
			new HashMap<String, String>());
	/**
	 * This data container holds all of the supported locales for this program. The was
	 * the key-value pairs for the container is structured is where the key is "ru_RU" and the
	 * value would be "Russia".
	 */
	public static final Map<String, String> MAP_LOCALE_CODES = Collections.synchronizedMap(
			new HashMap<String, String>());

	/**
	 * This data container holds all the category names. This data container is a key-value pair
	 * so the correct spelling of the category name is the key and the value is the category name
	 * in lower-case and all spaces in string are replaced with dashes "-".
	 */
	public static final Map<String, String> MAP_ISSUE_NAMES = 
			Collections.synchronizedMap(new HashMap<String, String>());
	
	public static final String[] GAME_INFORMATION_ISSUES = {"Availability", "Features", 
			"Game modes", "Getting started", "News", "Patch notes", "Tips and tricks"};
	public static final String[] CODES_AND_PROMO_ISSUES = {"Invalid code", "Lost code", "Promotion questions"};
	public static final String[] MANAGE_MY_ACCOUNT_ISSUES = {"Can't log in", "Delete account",
			"Find missing game", "Hacked account", "Manage account details", "Manage payment details",
			"Manage security settings", "Manage subscriptions", "Reset password", "Restore account",
			"Transfer between accounts"};
	public static final String[] MISSING_CONTENT_ISSUES = {"Had content and lost it", 
			"Never received content"};
	public static final String[] ORDERS_ISSUES = {"Cancel preorder", "Check order status", 
			"Dispute charge", "Order not received", "Report payment error", "Request refund",
			"Unable to purchase"};
	public static final String[] REPORT_BUG_ISSUES = {"Give feedback", "Report bug"};
	public static final String[] REPORT_HARASSMENT_ISSUES = {"Real-life threat", "Report player",
			"Report website"};
	public static final String[] TECHNICAL_SUPPORT_ISSUES = {"Connectivity", "Downloads",
			"Game performance", "Game progress issues", "Game won't launch", "Installation",
			"Request progress reset"};
	public static final String[] WARRANTY_ISSUES = {"New warranty request", "Warranty status", 
			"Existing Warranty", "Other"};
	
	// Global indexes used to access issues that belong to a specific topic.
	public static final String GAME_INFO = "Game information";
	public static final String CODES_AND_PROMO = "Codes and promotions";
	public static final String MANAGE_MY_ACCNT = "Manage my account";
	public static final String MISSING_CONTENT = "Missing content";
	public static final String ORDERS = "Order";
	public static final String REPORT_BUG = "Report a bug";
	public static final String REPORT_HARASSMENT = "Report concerns or harassment";
	public static final String TECHNICAL_SUPPORT = "Technical support";
	public static final String WARRANTY = "Warranty";
		
	// Global indexes used to access specific columns in the excel workbook.
	public static final int EXCEL_PRODUCT_COL = 0;
	public static final int EXCEL_CATEGORY_COL = 1;
	public static final int EXCEL_ISSUE_COL = 2;
	public static final int EXCEL_LOCALE_COL = 3;
	public static final int EXCEL_CHAT_COL = 4;
	public static final int EXCEL_PHONE_COL = 5;
	public static final int EXCEL_EMAIL_COL = 6;
	public static final int EXCEL_AHQ_COL = 7;
	public static final int EXCEL_OFF_HOUR_COL = 8;
	public static final int EXCEL_ERROR_COL = 9;
	public static final int EXCEL_NUM_COLS = 10;
	
	public static void main(String[] args)
	{
		load();
	}
	
	public static void load()
	{
		// Manage my account issues
		MAP_ISSUE_NAMES.put("can't log in", "cant-log-in");
		MAP_ISSUE_NAMES.put("delete account", "delete-account");
		MAP_ISSUE_NAMES.put("find missing game", "find-missing-game");
		MAP_ISSUE_NAMES.put("hacked account", "hacked-account");
		MAP_ISSUE_NAMES.put("manage account details", "manage-account-details");
		MAP_ISSUE_NAMES.put("manage payment details", "manage-payment-details");
		MAP_ISSUE_NAMES.put("manage security settings", "manage-security-settings");
		MAP_ISSUE_NAMES.put("manage subscriptions", "manage-subscriptions");
		MAP_ISSUE_NAMES.put("reset password", "reset-password");
		MAP_ISSUE_NAMES.put("restore account", "restore-account");
		MAP_ISSUE_NAMES.put("transfer between accounts", "transfer-between-accounts");
		
		// Codes and promotions issues.
		MAP_ISSUE_NAMES.put("promotion questions", "promotion-questions");
		MAP_ISSUE_NAMES.put("lost code", "lost-code");
		MAP_ISSUE_NAMES.put("invalid code", "invalid-code");
		
		// Orders issues.
		MAP_ISSUE_NAMES.put("order not received", "order-not-received");
		MAP_ISSUE_NAMES.put("check order status", "check-order-status");
		MAP_ISSUE_NAMES.put("request refund", "request-refund");
		MAP_ISSUE_NAMES.put("cancel preorder", "cancel-preorder");
		MAP_ISSUE_NAMES.put("dispute charge", "dispute-charge");
		MAP_ISSUE_NAMES.put("report payment error", "report-payment-error");
		MAP_ISSUE_NAMES.put("request refund", "request-refund");
		MAP_ISSUE_NAMES.put("unable to purchase", "unable-to-purchase");		
		
		// Missing content issues.
		MAP_ISSUE_NAMES.put("had content and lost it", "had-content-and-lost-it");
		MAP_ISSUE_NAMES.put("never received content", "never-received-content");
		
		// Game information issues.
		MAP_ISSUE_NAMES.put("availability", "availability");
		MAP_ISSUE_NAMES.put("features", "features");
		MAP_ISSUE_NAMES.put("game modes", "game-modes");
		MAP_ISSUE_NAMES.put("getting started", "getting-started");
		MAP_ISSUE_NAMES.put("request refund", "request-refund");
		MAP_ISSUE_NAMES.put("news", "news");
		MAP_ISSUE_NAMES.put("tips and tricks", "tips-and-tricks");
		MAP_ISSUE_NAMES.put("patch notes", "patch-notes");
		
		// Report a bug issues.
		MAP_ISSUE_NAMES.put("give feedback", "give-feedback");
		MAP_ISSUE_NAMES.put("report bug", "report-bug");
		
		// Report concerns or harassment issues.
		MAP_ISSUE_NAMES.put("real-life threat", "reallife-threat");
		MAP_ISSUE_NAMES.put("report player", "report-player");
		MAP_ISSUE_NAMES.put("report website", "report-website");
		
		// Technical support issues.
		MAP_ISSUE_NAMES.put("connectivity", "connectivity");
		MAP_ISSUE_NAMES.put("downloads", "downloads");
		MAP_ISSUE_NAMES.put("game performance", "game-performance");
		MAP_ISSUE_NAMES.put("game progress issues", "game-progress-issues");
		MAP_ISSUE_NAMES.put("game won't launch", "game-wont-launch");
		MAP_ISSUE_NAMES.put("installation", "installation");
		MAP_ISSUE_NAMES.put("request progress reset", "request-progress-reset");
		
		// Warranty issues.
		MAP_ISSUE_NAMES.put("new warranty request", "new-warranty-request");
		MAP_ISSUE_NAMES.put("warranty status", "warranty-status");
		
		MAP_LOCALE.put("United States", "en_US");
		MAP_LOCALE.put("Australia", "au_AU");
		MAP_LOCALE.put("Brazil", "pt_BR");
		MAP_LOCALE.put("Canada", "en_CA");
		MAP_LOCALE.put("Canada(French)", "fr_CA");
		MAP_LOCALE.put("Czech Republic", "cs_CZ");
		MAP_LOCALE.put("China", "zh_HK");
		MAP_LOCALE.put("Denmark", "da_DK");
		MAP_LOCALE.put("Germany", "de_DE");
		MAP_LOCALE.put("Spain", "es_ES");
		MAP_LOCALE.put("Finland", "fi_FI");
		MAP_LOCALE.put("France", "fr_FR");
		MAP_LOCALE.put("India", "en_IN");
		MAP_LOCALE.put("Italy", "it_IT");
		MAP_LOCALE.put("Japan", "ja_JP");
		MAP_LOCALE.put("Korean", "ko_KR");
		MAP_LOCALE.put("Hungary", "hu_HU");
		MAP_LOCALE.put("Mexico", "es_MX");
		MAP_LOCALE.put("Netherlands", "nl_NL");
		MAP_LOCALE.put("New Zealand", "en_NZ");
		MAP_LOCALE.put("Norway", "nb_NO");
		MAP_LOCALE.put("Poland", "pl_PL");
		MAP_LOCALE.put("Portugal", "pt_PT");
		MAP_LOCALE.put("Russia", "ru_RU");
		MAP_LOCALE.put("Switzerland(German)", "ch-de");
		MAP_LOCALE.put("Singapore", "en_SG");
		MAP_LOCALE.put("South Africa", "en_ZA");
		MAP_LOCALE.put("Suisse - French", "ch-fr");
		MAP_LOCALE.put("Sweden", "sv_SE");
		MAP_LOCALE.put("Svizzera - Italiano", "ch-it");
		MAP_LOCALE.put("Taiwan", "zh_TW");
		MAP_LOCALE.put("Turkey", "tr_TR");
		MAP_LOCALE.put("United Kingdom", "en_GB");
		
		/*
		// Locale names.
		Iterator<String> localeName_SetIterator = MAP_LOCALE.keySet().iterator();
		Iterator<String> localeCodes = MAP_LOCALE.values().iterator();
		
		
		// Assign values to map.
		// This assignment is the opposite of MAP_LOCALE where it's <localeCode, localeName>
		// instead of <localeName, localeCode> oriented.
		for(int i = 0; i < MAP_LOCALE.size(); ++i)
		{
			// Assign values to map.
			MAP_LOCALE_CODES.put(localeCodes.next() ,localeName_SetIterator.next());
		}
		*/
		
		System.out.println("Data Warehouse loading complete...");
	}
}
