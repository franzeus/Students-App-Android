package com.mmt.classes;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.mmt.sap.R;

public class SubjectListAdapter extends SimpleCursorAdapter {

	private GradeDbAdapter gradeAdapter;
	
    public SubjectListAdapter(Context context, Cursor cur, String[] from, int[] to) {
        super(context, R.layout.subject_row, cur, from, to);
        gradeAdapter = new GradeDbAdapter(context);
    }

    @Override
    public View newView(Context context, Cursor cur, ViewGroup parent) {
    	
    	final LayoutInflater li = LayoutInflater.from(context);
        return li.inflate(R.layout.subject_row, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cur) {
        
    	TextView titleText = (TextView)view.findViewById(R.id.text1);
    	TextView subText = (TextView)view.findViewById(R.id.info);
    	
    	CharSequence title = cur.getString(cur.getColumnIndex(SubjectDbAdapter.KEY_TITLE));   	          
    	int id = cur.getInt(cur.getColumnIndex(SubjectDbAdapter.KEY_ROWID));
		
    	// Average Grade of Subject
    	gradeAdapter.open();
    	String average = gradeAdapter.getAverageGrade(id, 0);
    	gradeAdapter.close();
    	            
        titleText.setText(title);
        subText.setText("Durchschnitt: " + average);
    }
}
