package com.mmt.classes;

import com.mmt.sap.R;

public class GradeHelper {
	
	// Returns the color of a grade value
	// Defined in res/values/colors.xml
	public int getGradeColor(String grade) {
		
		int gradeColor = R.color.def;
		
    	Integer gradeInt = new Integer(grade);
    	
        if(gradeInt == 1) {
        	gradeColor = R.color.one;
        } else if(gradeInt == 2) {
        	gradeColor = R.color.two;
        } else if(gradeInt == 3) {
        	gradeColor = R.color.three;
        } else if(gradeInt == 4) {
        	gradeColor = R.color.four;
        } else if(gradeInt == 5) {
        	gradeColor = R.color.five;
        } else if(gradeInt == 6) {
        	gradeColor = R.color.six;
        } else { 
        	gradeColor = R.color.def;
        }
		
        return gradeColor;
	}
}