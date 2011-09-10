package com.mmt.classes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Helper {

	// --------------------------------------------
	// Format Date to dd.mm.yyyy
	public String formatDate(String dateStr) {    	 
    	SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd"); 
    	Date dateObj = null;
		try {
			dateObj = curFormater.parse(dateStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    	SimpleDateFormat postFormater = new SimpleDateFormat("dd.MM.yyyy"); 
    	 
    	String newDateStr = postFormater.format(dateObj);
    	return newDateStr;
    	
    }	
}
