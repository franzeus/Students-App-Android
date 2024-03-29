package com.mmt.classes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.mmt.sap.R;

public class GradeListAdapter extends SimpleCursorAdapter {

    public GradeListAdapter(Context context, Cursor cur, String[] from, int[] to) {
        super(context, R.layout.notes_row, cur, from, to);
    }

    @Override
    public View newView(Context context, Cursor cur, ViewGroup parent) {
    	
    	final LayoutInflater li = LayoutInflater.from(context);
    	//LayoutInflater li = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return li.inflate(R.layout.notes_row, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cur) {
        
    	CharSequence date = cur.getString(cur.getColumnIndex(GradeDbAdapter.KEY_DATE));
    	TextView dateText = (TextView)view.findViewById(R.id.text1);
    	          
    	CharSequence subject = cur.getString(cur.getColumnIndex(GradeDbAdapter.KEY_SUBJECTNAME));
    	TextView subjectText = (TextView)view.findViewById(R.id.text2);

    	CharSequence type = cur.getString(cur.getColumnIndex(GradeDbAdapter.KEY_TYPE));
    	TextView typeText = (TextView)view.findViewById(R.id.text3);
    	
    	CharSequence grade = cur.getString(cur.getColumnIndex(GradeDbAdapter.KEY_GRADE));
    	TextView gradeText = (TextView)view.findViewById(R.id.text4);
    	
    	// Define Grade Color      	
        GradeHelper gradeHelper = new GradeHelper();
        int gradeColor = gradeHelper.getGradeColor(grade.toString());
        
        Helper helper = new Helper();
        
        gradeText.setBackgroundResource(gradeColor);
        gradeText.setText(grade);
        dateText.setText(helper.formatDate((String) date));
        typeText.setText(type);
        subjectText.setText(subject);
    }
    
    /*
    private String formatDate(String dateStr) {    	 
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
    	
    } */
}
